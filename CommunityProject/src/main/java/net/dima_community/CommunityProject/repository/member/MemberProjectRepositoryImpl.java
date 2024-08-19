package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
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
    public List<MemberProjectDTO> findByUsername(String memberId) {
        return memberProjectJPARepository.findByMemberId(memberId).stream().map(entity -> entity.toModel())
                .collect(Collectors.toList());
    }

    @Override
    public void save(MemberDTO member, MemberProjectDTO updatedMemberProject) {
        memberProjectJPARepository.save(MemberProjectEntity.from(updatedMemberProject, MemberEntity.toEntity(member)));
    }

    @Override
    public Optional<MemberProjectDTO> findById(Long id) {
        return memberProjectJPARepository.findById(id).map(entity -> entity.toModel());
    }

}
