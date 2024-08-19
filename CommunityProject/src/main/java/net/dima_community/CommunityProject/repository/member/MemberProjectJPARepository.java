package net.dima_community.CommunityProject.repository.member;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.member.MemberProjectEntity;

public interface MemberProjectJPARepository extends JpaRepository<MemberProjectEntity, Long> {

    @Query(value = "SELECT * FROM MEMBERPROJECT WHERE member_id LIKE %:memberId%", nativeQuery = true)
    List<MemberProjectEntity> findByMemberId(@Param("memberId") String memberId);

}
