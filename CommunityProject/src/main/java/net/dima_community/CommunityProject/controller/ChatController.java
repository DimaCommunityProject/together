
package net.dima_community.CommunityProject.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatMessage;
import net.dima_community.CommunityProject.service.ChatRoomService;
import net.dima_community.CommunityProject.service.ChatService;
import net.dima_community.CommunityProject.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.queue";

    private final ChatService chatService;
    private final RabbitMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;
    
    // Queue: Queue는 단일 소비자에게 메시지를 전달/ 주로 비동기적으로 메시지 처리 / 한번에 하나의 소비자에게 전달할 때 사용 
    // Topic: 며러 소비자에게 브로드캐스트 / 특정 주제에 대한 구독자들은 모두 해당 메시지 수신  
    
    
    /**
     * 대화 입장 시 알림 
     * @param chat
     * @param chatRoomId
     * @param headerAccessor
     */
    @MessageMapping("/chat.enter/{chatRoomId}")
    public void enterUser(@Payload ChatMessage chat, @DestinationVariable String chatRoomId, SimpMessageHeaderAccessor headerAccessor) {
        chat.setTimestamp(LocalDateTime.now().toString());
        chat.setContent(chat.getSenderId() + " 님 입장!!");
        chat.setRoomId(chatRoomId);

        // 메시지 발송
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, chat);

        log.info("User {} entered room {}", chat.getSenderId(), chatRoomId);
    }
    
    /**
     * Queue로 전달된 메시지를 수신하여 DB에 저장
     * 다시 브로드캐스트할 필요 없으므로 메시지 브로드캐스트 코드 제거 (queue)
     * 
     * @param chatMessage
     */
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void handleIncomingMessage(ChatMessage chatMessage) {
        try {
            chatService.saveMessage(chatMessage);
            log.info("Message saved to DB: {}", chatMessage.getContent());
        } catch (Exception e) {
            log.error("Error occurred while saving message to DB", e);
        }
    }
    
    /**
     * 메시지 전송 및 수신은 STOMP 프로트콜 사용하여 모든 클라이언트에게 실시간으로 메시지 브로드캐스트 (topic)
     * @param chat
     * @param chatRoomId
     */
    @MessageMapping("/chat.message/{chatRoomId}")
    public void sendMessage(@Payload ChatMessage chat, @DestinationVariable("chatRoomId") String chatRoomId) {
        chat.setTimestamp(LocalDateTime.now().toString());
        chat.setRoomId(chatRoomId);

        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, chat);

        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        
        log.info("Sent message to room {}: {}", chatRoomId, chat.getContent());
    }
    
    /**
     * 채팅방 선택 시 이전 메시지 확인하는 매서드 
     * @param roomId
     * @return
     */
    @GetMapping("/chatRoom/getMessages/{roomId}")
    @ResponseBody
    public List<String> getMessages(@PathVariable String roomId) {
        return chatService.getMessagesByRoomId(roomId)
                .stream()
                .map(ChatMessage::getContent) // 각 메시지의 내용만 반환
                .collect(Collectors.toList());
    }
    
    
    //==========================================================
    /**
     * 대화상대 접속상태 확인 
     * @param userId
     * @return
     */
    @GetMapping("/user/status/{userId}")
    @ResponseBody
    public boolean getUserStatus(@PathVariable String userId) {
        return chatService.isUserOnline(userId);
    }
    
}
