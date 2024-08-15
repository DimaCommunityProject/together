package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.repository.member.MemberProjectRepository;

@Service
@RequiredArgsConstructor
public class MemberProjectService {

    public final MemberProjectRepository memberProjectRepository;

    public MemberProjectDTO findByUsername(String memberId) {
        Optional<MemberProjectDTO> result = memberProjectRepository.findByUsername(memberId);
        if (result == null | !result.isPresent()) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return result.get();
        // return memberProjectRepository.findByUsername(memberId)
        // .orElseThrow(() -> new ResourceNotFoundException("MemberProject", memberId));
    }

    public MemberProjectDTO updateProject(MemberDTO updatedMember, MemberProjectDTO memberProject) {
        MemberProjectDTO originalMemberProject = null;
        try {
            originalMemberProject = findByUsername(updatedMember.getMemberId());
        } catch (ResourceNotFoundException e) {
            // 기존에 없으면 신규 저장
            memberProjectRepository.save(updatedMember, memberProject);
            return memberProject;
        }
        // 있으면 업데이트 후 저장
        MemberProjectDTO updatedMemberProject = originalMemberProject.update(memberProject);
        memberProjectRepository.save(updatedMember, updatedMemberProject);
        return updatedMemberProject;
    }

}
