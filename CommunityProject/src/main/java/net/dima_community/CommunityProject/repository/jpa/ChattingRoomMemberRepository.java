package net.dima_community.CommunityProject.repository.jpa;

import net.dima_community.CommunityProject.entity.ChattingRoomMemberEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRoomMemberRepository extends JpaRepository<ChattingRoomMemberEntity, Long> {
    List<ChattingRoomMemberEntity> findByMember_MemberId(String memberId);
}