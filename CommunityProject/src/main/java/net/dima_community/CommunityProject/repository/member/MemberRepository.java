package net.dima_community.CommunityProject.repository.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Query("SELECT count(*) FROM MemberEntity m WHERE m.memberEmail = :memberEmail AND m.memberId = :memberId")
    int findIdByMemberEmailAndMemberId(@Param("memberEmail") String memberEmail, @Param("memberId") String memberId);

    @Modifying // 데베 상태 바꿈
    @Transactional
    @Query("UPDATE MemberEntity m SET m.memberPw = :memberPw WHERE m.memberId = :memberId")
    int PwUpdate(@Param("memberId") String memberId, @Param("memberPw") String memberPw);
    
    //관리자 페이지에 보여줄 기수로 승인된 회원 찾기
    @Query("SELECT m FROM MemberEntity m WHERE m.memberGroup = :memberGroup AND m.memberEnabled = 'y'")
	Page<MemberEntity> findByMemberGroup(@Param("memberGroup") String memberGroup, PageRequest pageRequest);
    
    //관리자 페이지에 보여줄 승인 안된 회원 찾기
	List<MemberEntity> findByMemberEnabled(String enabled);

    Optional<MemberEntity> findByMemberEmail(String to);
    
    //memberService에서 회원가입 시 이메일 중복 확인 
    boolean existsByMemberEmail(String memberEmail);
}
