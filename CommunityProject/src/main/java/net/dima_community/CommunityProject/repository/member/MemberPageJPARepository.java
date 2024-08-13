package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.member.MemberPageEntity;

public interface MemberPageJPARepository extends JpaRepository<MemberPageEntity, Long> {

    @Query(value = "SELECT * FROM MEMBERPAGE WHERE member_id LIKE %:memberId%", nativeQuery = true)
    Optional<MemberPageEntity> findByMemberId(@Param("memberId") String memberId);

}
