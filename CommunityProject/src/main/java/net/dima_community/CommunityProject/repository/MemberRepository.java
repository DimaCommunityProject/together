package net.dima_community.CommunityProject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import net.dima_community.CommunityProject.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<MemberEntity> findByMemberId(String id);

    // 이름과 이메일로 아이디를 찾는 메서드
    @Query("SELECT COALESCE(m.memberId, '0') FROM MemberEntity m WHERE m.memberName = :memberName AND m.memberEmail = :memberEmail")
    String findIdByMemberNameAndMemberEmail(@Param("memberName") String memberName,
            @Param("memberEmail") String memberEmail);

    // 사용자 존재 확인
    // @Query("SELECT COALESCE(m.memberId, '0') FROM MemberEntity m WHERE
    // m.memberName = :memberName AND m.memberEmail = :memberEmail AND m.memberId =
    // :memberId")
    // String findIdByMemberNameAndMemberEmailAndMemberId(@Param("memberName")
    // String memberName, @Param("memberEmail") String memberEmail,
    // @Param("memberId") String memberId);

    // 사용자 존재 확인
    @Query("SELECT count(*) FROM MemberEntity m WHERE m.memberEmail = :memberEmail AND m.memberId = :memberId")
    int findIdByMemberEmailAndMemberId(@Param("memberEmail") String memberEmail, @Param("memberId") String memberId);

    @Modifying // 데베 상태 바꿈
    @Transactional
    @Query("UPDATE MemberEntity m SET m.memberPw = :memberPw WHERE m.memberId = :memberId")
    int PwUpdate(@Param("memberId") String memberId, @Param("memberPw") String memberPw);

    Optional<MemberEntity> findByMemberEmail(String to);
}
