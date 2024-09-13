package net.dima_community.CommunityProject.repository.board;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.dto.board.combine.BoardListDTO;
import net.dima_community.CommunityProject.entity.board.JobBoardEntity;


public interface JobBoardRepository extends JpaRepository<JobBoardEntity, Long>{

        // 카테고리에 해당하는 (신고당하지 않은) JobBoardEntities 반환 (최신순)
        @Query("SELECT j FROM JobBoardEntity j " +
                "JOIN j.boardEntity b " +
                "WHERE b.category = :category " +
                "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                "AND b.reported = false " +
                "ORDER BY b.createDate DESC")
        Page<JobBoardEntity> findByCategoryAndTitleContainingAndReportedIsFalse(@Param("category") BoardCategory category, 
                                                                                @Param("searchWord") String searchWord, 
                                                                                Pageable pageable); 

        // 카테고리에 해당하는 (신고당하지 않은) BoardListDTOs 반환 (최신순)
        @Query("SELECT new net.dima_community.CommunityProject.dto.board.combine.BoardListDTO(" +
        "b.boardId, " +
        "b.memberEntity.memberId, " +
        "b.memberGroup, " +
        "b.title, " +
        "b.hitCount, " +
        "b.likeCount, " +
        "b.replyCount, " +
        "b.createDate, " +
        "j.deadline, " +
        "j.limitNumber, " +
        "j.currentNumber) " +
        "FROM BoardEntity b " +
        "LEFT JOIN b.jobBoardEntity j " + // BoardEntity와 JobBoardEntity를 조인
        "WHERE b.category = :category " +
        "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
        "AND b.reported = false " +
        "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByCategoryAndTitleContainingAndReportedIsFalse(
        @Param("category") BoardCategory category, 
        @Param("searchWord") String searchWord, 
        Pageable pageable);

}
