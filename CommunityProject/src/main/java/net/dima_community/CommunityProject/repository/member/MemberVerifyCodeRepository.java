package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.dima_community.CommunityProject.entity.member.MemberVerifyCodeEntity;

@Repository
public interface MemberVerifyCodeRepository extends JpaRepository<MemberVerifyCodeEntity, Long> {

    @Query(value = "SELECT * FROM MEMBER_VERIFY_CODE WHERE member_id LIKE CONCAT('%', :memberId, '%')", nativeQuery = true)
    Optional<MemberVerifyCodeEntity> findByMemberId(@Param("memberId") String memberId);

    @Query(value = "DELETE FROM MEMBER_VERIFY_CODE WHERE member_id LIKE CONCAT('%', :memberId, '%')", nativeQuery = true)
    void deleteByMemberId(String memberId);

}