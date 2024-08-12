package net.dima_community.CommunityProject.service.member;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.repository.member.MemberProjectRepository;

@Service
@RequiredArgsConstructor
public class MemberProjectService {

    public final MemberProjectRepository memberProjectRepository;

    public MemberProjectDTO findByUsername(String memberId) {
        return memberProjectRepository.findByUsername(memberId);
    }

    public MemberProjectDTO updateProject(Member updatedMember, MemberProjectDTO memberProject) {
        MemberProjectDTO originalMemberProject = findByUsername(updatedMember.getMemberId());
        MemberProjectDTO updatedMemberProject = originalMemberProject.update(memberProject);
        memberProjectRepository.save(updatedMember, updatedMemberProject);
        return updatedMemberProject;
    }

}
