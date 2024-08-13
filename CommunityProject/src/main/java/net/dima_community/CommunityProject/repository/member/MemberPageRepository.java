package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.domain.Member;

public interface MemberPageRepository {

    Optional<MemberPageDTO> findByUsername(String memberId);

    void save(Member updatedMember, MemberPageDTO updatedMemberPage);

}
