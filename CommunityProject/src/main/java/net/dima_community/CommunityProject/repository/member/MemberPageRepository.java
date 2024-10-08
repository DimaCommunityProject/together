package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;

public interface MemberPageRepository {

    Optional<MemberPageDTO> findByUsername(String memberId);

    void save(MemberDTO member, MemberPageDTO memberPage);

}
