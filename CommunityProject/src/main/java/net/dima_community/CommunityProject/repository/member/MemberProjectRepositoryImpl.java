package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.member.MemberProjectEntity;

@Repository
@RequiredArgsConstructor
public class MemberProjectRepositoryImpl implements MemberProjectRepository {

    public final MemberProjectJPARepository memberProjectJPARepository;

    @Override
    public Optional<MemberProjectDTO> findByUsername(String memberId) {
        return memberProjectJPARepository.findByMemberId(memberId).map(entity -> entity.toModel());
    }

    @Override
    public void save(MemberDTO member, MemberProjectDTO updatedMemberProject) {
        memberProjectJPARepository.save(MemberProjectEntity.from(updatedMemberProject, MemberEntity.toEntity(member)));
    }

}
