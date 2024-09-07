
package net.dima_community.CommunityProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatMessage;
import net.dima_community.CommunityProject.repository.jpa.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.mongo.ChatMessageRepository;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * mongoDB에 저장하고 불러오는 서비스 
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
	
    private final ChatMessageRepository chatMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, Boolean> userStatusMap = new ConcurrentHashMap<>();

    
    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
    
    public void sendMessage(String exchange, String routingKey, ChatMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
            msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return msg;
        });
    }

    public void saveMessage(ChatMessage message) {
    	chatMessageRepository.save(message);
        try {
        	log.info("Attempting to save message: {}", message);
            message.setTimestamp(LocalDateTime.now().toString());
            chatMessageRepository.save(message);
            log.info("Message saved to DB: {}", message);
        } catch (Exception e) {
            log.error("Failed to save message to DB", e);
        }
    }
    
    
    // 사용자의 온라인 상태를 설정
    public void setUserOnlineStatus(String userId, boolean isOnline) {
        userStatusMap.put(userId, isOnline);
    }

    // 사용자의 온라인 상태를 확인
    public boolean isUserOnline(String userId) {
        return userStatusMap.getOrDefault(userId, false);
    }
    
    // 채팅방에 속한 멤버의 상태 관리 관련 메서드 추가
    public List<String> getRoomsByUser(String userId) {
        return chatMessageRepository.findDistinctRoomIdByUser(userId);
    }
    
    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
    

	
}
