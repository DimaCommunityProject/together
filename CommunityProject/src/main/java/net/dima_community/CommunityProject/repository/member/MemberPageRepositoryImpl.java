package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.entity.member.MemberPageEntity;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.infra.MemberEntity;

@Repository
@RequiredArgsConstructor
public class MemberPageRepositoryImpl implements MemberPageRepository {

    private final MemberPageJPARepository memberPageJPARepository;

    @Override
    public Optional<MemberPageDTO> findByUsername(String memberId) {
        return memberPageJPARepository.findByMemberId(memberId).map(entity -> entity.toModel());
    }

    @Override
    public void save(Member member, MemberPageDTO updatedMemberPage) {
        memberPageJPARepository.save(MemberPageEntity.from(updatedMemberPage, MemberEntity.from(member)));
    }

}
