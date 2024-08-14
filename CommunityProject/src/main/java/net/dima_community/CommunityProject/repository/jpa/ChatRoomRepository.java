package net.dima_community.CommunityProject.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.entity.ChatRoom;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByUniqueKey(String uniqueKey);

    List<ChatRoom> findByCreatedBy(String createdBy);
    
    // 특정 사용자가 속한 채팅방들을 가져오는 메서드
    List<ChatRoom> findByMemberIdsContaining(String userId);
}