package net.dima_community.CommunityProject.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.entity.member.MemberPageEntity;

public interface MemberPageJPARepository extends JpaRepository<MemberPageEntity, Long> {

    MemberPageEntity findByMemberId(String memberId);

}
