package net.dima_community.CommunityProject.member.service.port;

import java.util.Optional;

import net.dima_community.CommunityProject.member.domain.Member;

public interface MemberRepository {

    void save(Member member);

    Optional<Member> findByEmail(String to);

    void delete(Member member);

    Optional<Member> findById(String id);

}
