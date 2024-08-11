package net.dima_community.CommunityProject.service.member;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.repository.member.MemberPageRepository;

@Service
@RequiredArgsConstructor
public class MemberPageService {
    public final MemberPageRepository memberPageRepository;

    public MemberPageDTO findByUsername(Member member) {
        return memberPageRepository.findByUsername(member);
    }

}
