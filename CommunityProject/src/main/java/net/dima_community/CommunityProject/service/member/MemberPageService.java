package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.repository.member.MemberPageRepository;

@Service
@RequiredArgsConstructor
public class MemberPageService {
    public final MemberPageRepository memberPageRepository;

    public MemberPageDTO findByUsername(String memberId) {
        Optional<MemberPageDTO> result = memberPageRepository.findByUsername(memberId);
        if (result == null | !result.isPresent()) {
            throw new ResourceNotFoundException("Member", memberId);
        }
        return result.get();
        // return memberPageRepository.findByUsername(memberId)
        // .orElseThrow(() -> new ResourceNotFoundException("MemberPage", memberId));
    }

    public MemberPageDTO updatePage(Member updatedMember, MemberPageDTO memberPage) {
        MemberPageDTO originalMemberPage = null;
        try {
            originalMemberPage = findByUsername(updatedMember.getMemberId());
        } catch (ResourceNotFoundException e) {
            // 기존에 없으면 신규 저장
            memberPageRepository.save(updatedMember, memberPage);
            return memberPage;
        }
        // 있으면 업데이트 후 저장
        MemberPageDTO updatedMemberPage = originalMemberPage.update(memberPage);
        memberPageRepository.save(updatedMember, updatedMemberPage);
        return updatedMemberPage;
    }

}
