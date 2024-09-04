package net.dima_community.CommunityProject.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.chat.ChattingRoomMemberEntity;


public interface ChattingRoomMemberRepository extends JpaRepository<ChattingRoomMemberEntity, Long> {

    List<ChattingRoomMemberEntity> findByMember_MemberId(String memberId);
    
 // 특정 채팅방에 속한 멤버들의 deleted 상태 조회
    @Query("SELECT m FROM ChattingRoomMemberEntity m WHERE m.chatRoom.id = :roomId AND m.deleted = 0")
    List<ChattingRoomMemberEntity> findActiveMembersByChatRoomId(@Param("roomId") Long roomId);

    // 모든 멤버의 deleted 상태 포함 조회
    List<ChattingRoomMemberEntity> findByChatRoomId(Long roomId);
    
 // 특정 사용자가 속한 모든 채팅방의 멤버 정보 중 deleted가 0인 것만 가져오기
    List<ChattingRoomMemberEntity> findByMember_MemberIdAndDeleted(String memberId, int deleted);

    
}