package net.dima_community.CommunityProject.repository.board;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.JobBoardRecruitEntity;
import net.dima_community.CommunityProject.entity.board.MemberEntity;


public interface JobBoardRecruitRepository extends JpaRepository<JobBoardRecruitEntity,Long> {
    
    // 해당 recruit 게시글에서 특정 회원이 참여 신청을 했는지 확인하는 쿼리
    @Query("SELECT j FROM JobBoardRecruitEntity j "+
            "WHERE j.jobBoardEntity.boardEntity = :boardEntity AND j.memberEntity = :memberEntity")
    Optional<JobBoardRecruitEntity> findByBoardAndMember(@Param("boardEntity") BoardEntity boardEntity, @Param("memberEntity") MemberEntity memberEntity);

}
