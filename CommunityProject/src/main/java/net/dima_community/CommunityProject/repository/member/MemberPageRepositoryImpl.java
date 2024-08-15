package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.member.MemberPageEntity;

@Repository
@RequiredArgsConstructor
public class MemberPageRepositoryImpl implements MemberPageRepository {

    private final MemberPageJPARepository memberPageJPARepository;

    @Override
    public Optional<MemberPageDTO> findByUsername(String memberId) {
        return memberPageJPARepository.findByMemberId(memberId).map(entity -> entity.toModel());
    }

    @Override
    public void save(MemberDTO member, MemberPageDTO updatedMemberPage) {
        memberPageJPARepository.save(MemberPageEntity.from(updatedMemberPage, MemberEntity.toEntity(member)));
    }

}
