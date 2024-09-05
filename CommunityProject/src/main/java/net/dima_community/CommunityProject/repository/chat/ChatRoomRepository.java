package net.dima_community.CommunityProject.repository.chat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.chat.ChatRoom;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByUniqueKey(String uniqueKey);

    List<ChatRoom> findByCreatedBy(String createdBy);
    
    // 특정 채팅방에 있는 멤버를 조회하는 메서드
    @Query("SELECT m.member.memberId FROM ChattingRoomMemberEntity m WHERE m.chatRoom.id = :chatRoomId")
    List<String> findMemberIdsByChatRoomId(@Param("chatRoomId") Long chatRoomId);
}