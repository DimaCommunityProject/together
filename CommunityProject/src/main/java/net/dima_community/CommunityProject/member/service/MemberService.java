package net.dima_community.CommunityProject.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.port.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    // public final Member member;
    public final MemberRepository memberRepository;
    public final BCryptEncoderHolder bCryptEncoderHolder;

    public Member findById(String id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member == null || !member.isPresent()) {
            throw new ResourceNotFoundException("Member", id);
        }
        return member.get();
    }

    public void saveMemberWithVerificationCode(Member member, String verifyCode) {
        Member result = member.updateVerifyCode(verifyCode);
        memberRepository.save(result);
    }

    public boolean verifyMemberByCode(String to, String code) {
        Optional<Member> result = memberRepository.findByEmail(to);
        if (result == null || !result.isPresent()) {
            throw new ResourceNotFoundException("Member", to);
        }
        Member member = result.get();
        if (!code.equals(member.getMemberVerifyCode())) {
            memberRepository.delete(member);
            return false;
        }
        return true;
    }

    public Member setEncodedPassword(Member member) {
        member.setEncodedPassword(bCryptEncoderHolder);
        return member;

    }

    public void approve(String id) {
        Member member = findById(id);
        member.enabledToYes();
        memberRepository.save(member);
    }

}
