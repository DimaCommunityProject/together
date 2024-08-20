
package net.dima_community.CommunityProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatMessage;
import net.dima_community.CommunityProject.repository.mongo.ChatMessageRepository;

import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mongoDB에 저장하고 불러오는 서비스 
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
	

    private final ChatMessageRepository chatMessageRepository;
    
    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
    
    

    public void saveMessage(ChatMessage message) {
        try {
        	log.info("Attempting to save message: {}", message);
            message.setTimestamp(LocalDateTime.now().toString());
            chatMessageRepository.save(message);
            log.info("Message saved to DB: {}", message);
        } catch (Exception e) {
            log.error("Failed to save message to DB", e);
        }
    }
    
    private final Map<String, Boolean> onlineUsers = new ConcurrentHashMap<>();

    public boolean isUserOnline(String userId) {
        return onlineUsers.getOrDefault(userId, false);
    }

    public void setUserOnline(String userId) {
        onlineUsers.put(userId, true);
    }

    public void setUserOffline(String userId) {
        onlineUsers.put(userId, false);
    }
    
    public List<String> getRoomsByUser(String userId) {
        return chatMessageRepository.findDistinctRoomIdByUser(userId);
    }
    
    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
    
	
}
