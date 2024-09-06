package net.dima_community.CommunityProject.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
	private final ChatRoomService chatRoomService;
	private final ChatService chatService;
	private final RabbitTemplate rabbitTemplate;

	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";

	@MessageMapping("/chat.enter/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage enterRoom(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessage message) {
	    message.setSenderName(chatRoomService.getUserNameByUserId(message.getSenderId()));
	    message.setTimestamp(LocalDateTime.now().toString());
	    message.setContent(message.getSenderName() + "님이 입장하셨습니다.");
	    message.setRoomId(chatRoomId);  // roomId 설정
	    chatService.setUserOnlineStatus(message.getSenderId(), true);
	    rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);
	    return message;
	}

	@MessageMapping("/chat.exit/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage exitRoom(@DestinationVariable("chatRoomId") Long chatRoomId, ChatMessage message) {
	    message.setSenderName(chatRoomService.getUserNameByUserId(message.getSenderId()));
	    message.setTimestamp(LocalDateTime.now().toString());
	    message.setContent(message.getSenderName() + "님이 퇴장하셨습니다.");
	    message.setRoomId(chatRoomId);  // roomId 설정
	    chatService.setUserOnlineStatus(message.getSenderId(), false);
	    rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);
	    return message;
	}

	@MessageMapping("/chat.message/{chatRoomId}")
	@SendTo("/queue/chat.room.{chatRoomId}")
	public ChatMessage sendMessage(
			@DestinationVariable("chatRoomId") Long chatRoomId, 
			ChatMessage message) {
	    message.setTimestamp(LocalDateTime.now().toString());
	    message.setRoomId(chatRoomId);
	    
	    String senderName = message.getSenderName();
	    message.setSenderName(senderName);
	    
	    // 메시지를 MongoDB에 저장
	    chatService.saveMessage(message, chatRoomId);  // roomId를 함께 전달
	    
	    rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);
	    return message;
	}
	
	
}