package net.dima_community.CommunityProject.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import net.dima_community.CommunityProject.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query(value="{ 'senderId': ?0 }", fields="{ 'roomId' : 1 }")
    List<String> findDistinctRoomIdByUser(String userId);

    List<ChatMessage> findByRoomId(String roomId);
    
    // 특정 채팅방의 메시지 수를 세는 메소드
    long countByRoomId(String roomId);
}