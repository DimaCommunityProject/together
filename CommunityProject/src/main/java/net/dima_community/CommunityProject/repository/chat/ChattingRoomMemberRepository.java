package net.dima_community.CommunityProject.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;


public interface ChattingRoomMemberRepository extends JpaRepository<ChattingRoomMemberEntity, Long> {

    List<ChattingRoomMemberEntity> findByMember_MemberId(String memberId);
    
    @Query("SELECT m FROM ChattingRoomMemberEntity m WHERE m.chatRoom.id = :roomId")
    List<ChattingRoomMemberEntity> findByChatRoomId(@Param("roomId") Long roomId);
}