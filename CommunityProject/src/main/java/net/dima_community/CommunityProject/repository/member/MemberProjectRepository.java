package net.dima_community.CommunityProject.repository.member;

import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.member.domain.Member;

public interface MemberProjectRepository {

    MemberProjectDTO findByUsername(String memberId);

    void save(Member updatedMember, MemberProjectDTO updatedMemberProject);

}
