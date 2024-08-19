package net.dima_community.CommunityProject.service.member;

import java.util.List;
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

    public List<MemberProjectDTO> findByUsername(String memberId) {
        List<MemberProjectDTO> result = memberProjectRepository.findByUsername(memberId);
        if (result == null | result.size() == 0) {
            throw new ResourceNotFoundException("MemberProject", memberId);
        }
        return result;
        // return memberProjectRepository.findByUsername(memberId)
        // .orElseThrow(() -> new ResourceNotFoundException("MemberProject", memberId));
    }

    public MemberProjectDTO findById(Long id) {
        Optional<MemberProjectDTO> result = memberProjectRepository.findById(id);
        if (result == null | !result.isPresent()) {
            throw new ResourceNotFoundException("MemberProject", "" + id);
        }
        return result.get();
    }

    public MemberProjectDTO updateProject(MemberDTO updatedMember, MemberProjectDTO memberProject) {
        List<MemberProjectDTO> originalMemberProject = null;
        try {
            originalMemberProject = findByUsername(updatedMember.getMemberId());
        } catch (ResourceNotFoundException e) {
            // 기존에 없으면 신규 저장
            memberProjectRepository.save(updatedMember, memberProject);
            return memberProject;
        }
        // 있으면 업데이트 후 저장
        // MemberProjectDTO updatedMemberProject =
        // originalMemberProject.update(memberProject);
        // memberProjectRepository.save(updatedMember, updatedMemberProject);
        // return updatedMemberProject;
        return null;
    }

}
