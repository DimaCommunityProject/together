
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
import org.springframework.messaging.handler.annotation.SendTo;
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
    
    private final ChatService chatService;
    private final RabbitMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;

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
    
    // 채팅방 선택 시 큐에서 메시지 수신
    public void receiveMessagesFromQueue(String queueName) {
        try {
            boolean keepReceiving = true;
            while (keepReceiving) {
                ChatMessage message = (ChatMessage) rabbitTemplate.receiveAndConvert(queueName);
                if (message != null) {
                    chatService.saveMessage(message);
                    log.info("Message from {} saved to DB: {}", queueName, message.getContent());
                } else {
                    keepReceiving = false;  // 더 이상 메시지가 없으면 루프 종료
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while receiving messages from queue", e);
        }
    }
    
    /**
     * 메시지 전송 및 수신은 STOMP 프로토콜 사용하여 모든 클라이언트에게 실시간으로 메시지 브로드캐스트 (topic)
     * 메시지는 또한 RabbitMQ를 통해 전달되고, MongoDB에 저장
     * @param chat
     * @param chatRoomId
     */
    @MessageMapping("/chat.message/{chatRoomId}")@SendTo("/queue/chat.room.{chatRoomId}")
    public ChatMessage sendMessage(@DestinationVariable("chatRoomId") String chatRoomId, ChatMessage message) {
        log.info("sendMessage method called with chatRoomId: {} and message: {}", chatRoomId, message.getContent());
        try {
            message.setTimestamp(LocalDateTime.now().toString());
            message.setRoomId(chatRoomId);

                // RabbitMQ로 메시지 전달
                String routingKey = "room." + chatRoomId;
                rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, routingKey, message);

                // 메시지를 MongoDB에 저장
                chatService.saveMessage(message);

                log.info("Sent message to room {}: {}", chatRoomId, message.getContent());
            } catch (Exception e) {
                log.error("Error in sendMessage: ", e);
            }

            return message;
        }

    
    /**
     * 채팅방 선택 시 이전 메시지 확인하는 매서드 
     * @param roomId
     * @return
     */
    @GetMapping("/chatRoom/getMessages/{roomId}")@ResponseBody
    public List<ChatMessage> getMessages(@PathVariable("roomId") String roomId) {// 채팅방에 연결된 큐 이름
        String queueName = "chat.room." + roomId;

            // 큐에서 메시지 수신
            receiveMessagesFromQueue(queueName);

            // MongoDB에서 저장된 메시지 가져오기
            return chatService.getMessagesByRoomId(roomId);

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