
package net.dima_community.CommunityProject.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.chat.ChatMessage;
import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.chat.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.service.chat.ChatService;
import net.dima_community.CommunityProject.service.member.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private final MemberService memberService;
    private final ChatService chatService;
    private final ChattingRoomMemberRepository chattingRoomMemberRepository;
    private final RabbitTemplate rabbitTemplate;
    // private final RabbitMessagingTemplate rabbitMessagingTemplate;

    /**
     * 입장 시 알림 전송
     * 
     * @param chatRoomId
     * @param message
     * @return
     */
    @Transactional
    @MessageMapping("/chat.enter/{chatRoomId}")
    @SendTo("/queue/chat.room.{chatRoomId}")
    public ChatMessage enterRoom(@DestinationVariable("chatRoomId") String chatRoomId, ChatMessage message) {
        try {
            String senderName = memberService.findByMemberId(message.getSenderId()).getMemberName();
            message.setSenderName(senderName);
            message.setTimestamp(LocalDateTime.now().toString());
            message.setRoomId(chatRoomId);
            message.setContent(senderName + "님이 입장하셨습니다.");

            // 사용자 상태를 'online'으로 설정
            chatService.setUserOnlineStatus(message.getSenderId(), true);

            // RabbitMQ로 입장 알림 전송
            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);

            // 모든 사용자에게 상태 업데이트 알림
            notifyUsersInRoom(chatRoomId);

            return message;
        } catch (Exception e) {
            log.error("Error in enterRoom: ", e);
            return null;
        }
    }

    /**
     * 퇴장 알림 전송
     * 
     * @param chatRoomId
     * @param message
     * @return
     */
    @MessageMapping("/chat.exit/{chatRoomId}")
    @SendTo("/queue/chat.room.{chatRoomId}")
    public ChatMessage exitRoom(@DestinationVariable("chatRoomId") String chatRoomId, ChatMessage message) {
        try {
            String senderName = memberService.findByMemberId(message.getSenderId()).getMemberName();
            message.setSenderName(senderName);
            message.setTimestamp(LocalDateTime.now().toString());
            message.setRoomId(chatRoomId);
            message.setContent(senderName + "님이 퇴장하셨습니다.");

            // 사용자 상태를 'offline'으로 설정
            chatService.setUserOnlineStatus(message.getSenderId(), false);

            // RabbitMQ로 퇴장 알림 전송
            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, message);

            // 채팅방에 있는 모든 사용자에게 현재 상태를 전송
            notifyUsersInRoom(chatRoomId);

            return message;
        } catch (Exception e) {
            log.error("Error in exitRoom: ", e);
            return null;
        }
    }

    /**
     * 특정 채팅방에 있는 사용자들에게 현재 사용자 상태 알림
     * 
     * @param chatRoomId
     */
    @Transactional
    private void notifyUsersInRoom(String chatRoomId) {
        Long chatRoomIdLong = Long.parseLong(chatRoomId);
        List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(chatRoomIdLong);

        members.forEach(member -> {
            MemberEntity memberEntity = member.getMember();
            String memberId = memberEntity.getMemberId();
            String memberName = memberEntity.getMemberName();
            boolean isOnline = chatService.isUserOnline(memberId);

            ChatMessage statusMessage = new ChatMessage();
            statusMessage.setSenderId(memberId);
            statusMessage.setRoomId(chatRoomId);
            statusMessage.setContent(memberName + " is " + (isOnline ? "online" : "offline"));

            rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + chatRoomId, statusMessage);
        });
    }

    /**
     * 특정 채팅방의 멤버 목록과 상태를 반환
     * 
     * @param roomId
     * @return
     */
    @GetMapping("/getRoomMembers/{roomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomMembers(@PathVariable("roomId") Long roomId) {
        try {
            // 해당 채팅방에 속한 모든 멤버 가져오기
            List<ChattingRoomMemberEntity> members = chattingRoomMemberRepository.findByChatRoomId(roomId);

            // 멤버의 상태 정보 구성
            List<Map<String, Object>> memberDetails = members.stream()
                    .map(member -> {
                        Map<String, Object> details = new HashMap<>();
                        String memberId = member.getMember().getMemberId();
                        details.put("memberId", memberId);
                        details.put("name", member.getMember().getMemberName());
                        details.put("status", chatService.isUserOnline(memberId) ? "online" : "offline");
                        return details;
                    })
                    .collect(Collectors.toList());

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("members", memberDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 메시지 전송 및 수신은 STOMP 프로토콜 사용하여 모든 클라이언트에게 실시간으로 메시지 브로드캐스트 (topic)
     * 메시지는 또한 RabbitMQ를 통해 전달되고, MongoDB에 저장
     * 
     * @param chat
     * @param chatRoomId
     */
    @MessageMapping("/chat.message/{chatRoomId}")
    @SendTo("/queue/chat.room.{chatRoomId}")
    public ChatMessage sendMessage(@DestinationVariable("chatRoomId") String chatRoomId, ChatMessage message) {
        log.info("sendMessage method called with chatRoomId: {} and message: {}",
                chatRoomId, message.getContent());
        try {
            message.setTimestamp(LocalDateTime.now().toString());
            message.setRoomId(chatRoomId);

            // senderName을 가져와서 메시지에 추가
            String senderName = memberService.findByMemberId(message.getSenderId()).getMemberName();
            message.setSenderName(senderName);

            // 메시지를 MongoDB에 저장
            chatService.saveMessage(message);

            // RabbitMQ로 메시지 전송
//            rabbitTemplate.convertAndSend("exchangeName", "routingKey", message);

            log.info("Sent message to room {}: {}", chatRoomId, message.getContent());

        } catch (Exception e) {
            log.error("Error in sendMessage: ", e);
        }
        // 메시지 반환
        return message;
    }

    /**
     * 채팅방 선택 시 이전 메시지 확인하는 매서드
     * 
     * @param roomId
     * @return
     */
    @GetMapping("/chatRoom/getMessages/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable("roomId") String roomId) {
        // 채팅방에 연결된 큐 이름
        // String queueName = "chat.room." + roomId;

        // 큐에서 메시지 수신
        // receiveMessagesFromQueue(queueName);

        // MongoDB에서 저장된 메시지 가져오기
        return chatService.getMessagesByRoomId(roomId);

    }

}