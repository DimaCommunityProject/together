package net.dima_community.CommunityProject.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
	boolean existsByMemberId(String memberId); // member_id로 중복 확인
    Optional<MemberEntity> findByMemberId(String memberId); // member_id로 회원 찾기

	// 이름과 이메일로 아이디를 찾는 메서드
    @Query("SELECT COALESCE(m.memberId, '0') FROM MemberEntity m WHERE m.memberName = :memberName AND m.memberEmail = :memberEmail")
    String findIdByMemberNameAndMemberEmail(@Param("memberName") String memberName, @Param("memberEmail") String memberEmail);
	
}
