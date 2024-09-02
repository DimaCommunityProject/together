package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.repository.member.MemberPageRepository;
import net.dima_community.CommunityProject.service.member.MemberService;

@Service
@RequiredArgsConstructor
public class MemberPageService {
    public final MemberPageRepository memberPageRepository;
    public final MemberService memberService;

    public MemberPageDTO findByUsername(String memberId) {
        Optional<MemberPageDTO> result = memberPageRepository.findByUsername(memberId);
        if (result == null | !result.isPresent()) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return result.get();
        // return memberPageRepository.findByUsername(memberId)
        // .orElseThrow(() -> new ResourceNotFoundException("MemberPage", memberId));
    }

    public MemberPageDTO updatePage(String memberId, MemberPageDTO memberPage) {
        MemberPageDTO originalMemberPage = null;
        MemberDTO memberDTO = memberService.findById(memberId);
        try {
            originalMemberPage = findByUsername(memberDTO.getMemberId());
        } catch (ResourceNotFoundException e) {
            // 기존에 없으면 신규 저장
            memberPageRepository.save(memberDTO, memberPage);
            return memberPage;
        }
        // 있으면 업데이트 후 저장
        MemberPageDTO updatedMemberPage = originalMemberPage.update(memberPage);
        memberPageRepository.save(memberDTO, updatedMemberPage);
        return updatedMemberPage;
    }

    public boolean updateMemberSkill(String memberId, String memberSkill) {
        MemberDTO member = null;
        MemberPageDTO originalDTO = null;
        try {
            originalDTO = findByUsername(memberId);
            member = memberService.findById(memberId);
        } catch (Exception e) {
            return false;
        }
        MemberPageDTO updatedDTO = originalDTO.updateSkill(memberSkill);
        memberPageRepository.save(member, updatedDTO);
        return true;

    }

}
