package net.dima_community.CommunityProject.repository.member;

import java.util.List;
import java.util.Optional;

import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;

public interface MemberProjectRepository {

    List<MemberProjectDTO> findByUsername(String memberId);

    void save(MemberDTO updatedMember, MemberProjectDTO updatedMemberProject);

    Optional<MemberProjectDTO> findById(Long id);

    void deleteById(Long projectSeq);

}
