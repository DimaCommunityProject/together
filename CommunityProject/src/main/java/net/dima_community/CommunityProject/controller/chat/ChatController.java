package net.dima_community.CommunityProject.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.chat.StatusUpdateMessage;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.service.chat.ChatRoomService;
import net.dima_community.CommunityProject.service.chat.ChatService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ChatController - 채팅 관련 실시간 메시지 전달 및 이벤트 처리를 담당하는 컨트롤러.
 * WebSocket을 통해 입장, 퇴장, 메시지 전송 등의 채팅 이벤트를 관리.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService; // 채팅방 관련 서비스
	private final ChatService chatService; // 채팅 메시지 및 사용자 상태 관련 서비스
	private final RabbitTemplate rabbitTemplate; // RabbitMQ 템플릿, 메시지 큐 전송에 사용

	private static final String CHAT_EXCHANGE_NAME = "chat.exchange"; // RabbitMQ의 교환기 이름

	// =======================================================
	// ===================== 채팅페이지 입장 =====================
	// =======================================================
	@MessageMapping("/statusUpdate") // 전체 회원이 구독
	@SendTo("/topic/statusUpdates")
	public StatusUpdateMessage sendStatusUpdate(StatusUpdateMessage message) {
		// 전체 구독자에게 상태 업데이트 메시지를 전송
		return message;
	}

	// =======================================================
	// ===================== 채팅방 입장 =====================
	// =======================================================

	/**
	 * 사용자가 채팅방에 입장할 때 처리하는 메서드.
	 * 
	 * @param chatRoomId 입장할 채팅방의 ID
	 * @param message    입장한 사용자의 메시지 객체
	 * @return 입장 메시지가 포함된 ChatMessage 객체
	 */
	@MessageMapping("/chat.enter/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage enterRoom(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessage message) {
		return processRoomAction(chatRoomId, message, "님이 해당 방에 접속하셨습니다.", true);
	}

	// =======================================================
	// ===================== 채팅방 퇴장 =====================
	// =======================================================

	/**
	 * 사용자가 채팅방에서 퇴장할 때 처리하는 메서드.
	 * 
	 * @param chatRoomId 퇴장할 채팅방의 ID
	 * @param message    퇴장한 사용자의 메시지 객체
	 * @return 퇴장 메시지가 포함된 ChatMessage 객체
	 */
	@MessageMapping("/chat.exit/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage exitRoom(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessage message) {
		return processRoomAction(chatRoomId, message, "님이 해당 방에 로그아웃 하셨습니다.", false);
	}

	// =======================================================
	// ===================== 메시지 전송 =====================
	// =======================================================

	/**
	 * 채팅방에서 메시지를 전송할 때 처리하는 메서드.
	 * 
	 * @param chatRoomId 메시지를 전송할 채팅방의 ID
	 * @param message    사용자가 보낸 메시지 객체
	 * @return 전송된 메시지 객체
	 */
	@MessageMapping("/chat.message/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessage message) {
		// 메시지의 타임스탬프와 채팅방 ID 설정
		message.setTimestamp(LocalDateTime.now().toString());
		message.setRoomId(chatRoomId);

		// 메시지를 MongoDB에 저장
		chatService.saveMessage(message, chatRoomId);

		// 메시지를 RabbitMQ로 전송
		sendToChatRoom(chatRoomId, message);
		return message;
	}

	// =======================================================
	// ===================== 공통 처리 메서드 =====================
	// =======================================================

	/**
	 * 입장/퇴장 시의 공통 로직을 처리하는 메서드.
	 * 
	 * @param chatRoomId    채팅방 ID
	 * @param message       메시지 객체
	 * @param actionMessage 입장/퇴장 메시지 (예: "님이 입장하셨습니다.")
	 * @param isEntering    입장 여부 (true = 입장, false = 퇴장)
	 * @return 처리된 메시지 객체
	 */
	private ChatMessage processRoomAction(Long chatRoomId, ChatMessage message, String actionMessage,
			boolean isEntering) {
		// 메시지 발신자의 이름을 채팅방 서비스에서 가져와 설정
		message.setSenderName(chatRoomService.getUserNameByUserId(message.getSenderId()));

		// 메시지의 타임스탬프와 입장/퇴장 메시지 설정
		message.setTimestamp(LocalDateTime.now().toString());
		message.setContent(message.getSenderName() + actionMessage);
		message.setRoomId(chatRoomId);

		// 사용자의 온라인 상태를 업데이트
		chatService.setUserOnlineStatus(message.getSenderId(), isEntering);

		// 메시지를 RabbitMQ로 전송
		sendToChatRoom(chatRoomId, message);
		return message;
	}

	// =======================================================
	// ===================== RabbitMQ 메시지 전송 =====================
	// =======================================================

	/**
	 * RabbitMQ로 메시지를 전송하는 메서드.
	 * 
	 * @param chatRoomId 채팅방 ID
	 * @param message    전송할 메시지 객체
	 */
	private void sendToChatRoom(Long chatRoomId, ChatMessage message) {
		rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);
	}
}