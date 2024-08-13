package net.dima_community.CommunityProject.member.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.port.MemberRepository;

@Repository
@RequiredArgsConstructor
@Builder
public class MemberRepositoryImpl implements MemberRepository {

    public final MemberJpaRepository memberJpaRepository;

    @Override
    public void save(Member member) {
        memberJpaRepository.save(MemberEntity.from(member));
    }

    @Override
    public Optional<Member> findByEmail(String to) {
        return memberJpaRepository.findByMemberEmail(to).map(entity -> entity.toModel());
    }

    @Override
    public void delete(Member member) {
        memberJpaRepository.deleteById(member.getMemberId());
    }

    @Override
    public Optional<Member> findById(String id) {
        return memberJpaRepository.findById(id).map(entity -> entity.toModel());
    }

}
