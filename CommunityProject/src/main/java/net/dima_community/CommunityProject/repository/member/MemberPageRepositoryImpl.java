package net.dima_community.CommunityProject.repository.member;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.domain.Member;

@Repository
@RequiredArgsConstructor
public class MemberPageRepositoryImpl implements MemberPageRepository {

    private final MemberPageJPARepository memberPageJPARepository;

    @Override
    public MemberPageDTO findByUsername(Member member) {
        return memberPageJPARepository.findByMemberId(member.getMemberId()).toModel();
    }

}
