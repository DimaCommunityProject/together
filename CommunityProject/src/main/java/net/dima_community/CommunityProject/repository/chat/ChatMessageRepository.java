package net.dima_community.CommunityProject.repository.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import net.dima_community.CommunityProject.entity.chat.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query(value="{ 'senderId': ?0 }", fields="{ 'roomId' : 1 }")
    List<String> findDistinctRoomIdByUser(String userId);

    List<ChatMessage> findByRoomId(Long roomId);
    
    // 특정 채팅방의 메시지 수를 세는 메소드
    long countByRoomId(String roomId);
    
 // MongoDB에서 roomId로 채팅 메시지 삭제
    void deleteByRoomId(Long roomId);
}