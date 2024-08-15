package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;

public interface MemberProjectRepository {

    Optional<MemberProjectDTO> findByUsername(String memberId);

    void save(MemberDTO updatedMember, MemberProjectDTO updatedMemberProject);

}
