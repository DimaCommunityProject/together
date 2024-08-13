package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.member.domain.Member;

public interface MemberProjectRepository {

    Optional<MemberProjectDTO> findByUsername(String memberId);

    void save(Member updatedMember, MemberProjectDTO updatedMemberProject);

}
