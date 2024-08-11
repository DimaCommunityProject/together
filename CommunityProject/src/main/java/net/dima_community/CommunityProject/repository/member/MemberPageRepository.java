package net.dima_community.CommunityProject.repository.member;

import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.domain.Member;

public interface MemberPageRepository {

    MemberPageDTO findByUsername(Member member);

}
