package net.dima_community.CommunityProject.repository.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.dto.board.combine.BoardListDTO;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.member.MemberEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

        // BoardEntity의 MemberEntity와 전달받은 memberEntity와 일치하는 게시글을 리스트로 반환
        @Query("SELECT b FROM BoardEntity b WHERE b.memberEntity = :memberEntity")
        List<BoardEntity> findByMemberId(@Param("memberEntity") MemberEntity memberEntity);

        // 카테고리가 group이고, 전달받은 memberGroup에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
        @Query("SELECT b FROM BoardEntity b WHERE " +
                        "b.category = 'group' AND " +
                        "b.memberGroup = :userGroup AND " +
                        "b.reported = false AND " +
                        "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
        Page<BoardEntity> findByMemberGroupAndNotReportedAndTitleContaining(@Param("userGroup") String userGroup,
                        @Param("searchWord") String searchWord, Pageable pageRequest);

        // group 게시판 목록 (반환 타입 : BoardListDTO)
        @Query("SELECT new net.dima_community.CommunityProject.dto.board.combine.BoardListDTO(" +
                        "b.boardId, " +
                        "b.memberEntity.memberId, " +
                        "b.memberGroup, " +
                        "b.title, " +
                        "b.hitCount, " +
                        "b.likeCount, " +
                        "b.replyCount, " +
                        "b.createDate, " +
                        "NULL, " + // deadline
                        "0, " + // limitNumber
                        "0) " + // currentNumber
                        "FROM BoardEntity b " +
                        "WHERE b.category = 'group' " +
                        "AND b.memberGroup = :userGroup " +
                        "AND b.reported = false " +
                        "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                        "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByMemberGroupAndTitleContaining(@Param("userGroup") String userGroup,
                        @Param("searchWord") String searchWord,
                        Pageable pageRequest);

        // 카테고리에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
        @Query("SELECT b FROM BoardEntity b WHERE " +
                        "b.category = :category AND " +
                        "b.reported = false AND " +
                        "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
        Page<BoardEntity> findByCategoryTitleContainingAndReportedIsFalse(@Param("category") BoardCategory category,
                        @Param("searchWord") String searchWord, Pageable pageRequest);

        // code/project/free/info 게시판 목록(반환 타입 : BoardListDTO)
        @Query("SELECT new net.dima_community.CommunityProject.dto.board.combine.BoardListDTO(" +
                        "b.boardId, " +
                        "b.memberEntity.memberId, " +
                        "b.memberGroup, " +
                        "b.title, " +
                        "b.hitCount, " +
                        "b.likeCount, " +
                        "b.replyCount, " +
                        "b.createDate, " +
                        "NULL, " + // deadline
                        "0, " + // limitNumber
                        "0) " + // currentNumber
                        "FROM BoardEntity b " +
                        "WHERE b.category = :category " +
                        "AND b.reported = false " +
                        "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                        "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByCategoryAndTitleContaining(@Param("category") BoardCategory category,
                        @Param("searchWord") String searchWord,
                        Pageable pageRequest);

        // boardId에 해당하는 Entity의 likeCount를 1 증가시킴
        @Modifying
        @Query("UPDATE BoardEntity b SET b.likeCount = b.likeCount + 1 WHERE b.boardId = :boardId")
        void incrementLikeCount(Long boardId);

        // boardId에 해당하는 Entity의 likeCount를 1 감소시킴 (연산 시 likeCount가 0보다 작은 경우는 0으로 세팅)
        @Modifying
        @Query("UPDATE BoardEntity b " +
                        "SET b.likeCount = CASE WHEN b.likeCount - 1 < 0 THEN 0 ELSE b.likeCount - 1 END " +
                        "WHERE b.boardId = :boardId")
        void decrementLikeCount(Long boardId);

        // hitcount 제일 많은 게시글 3개 불러오기
        @Query("SELECT b FROM BoardEntity b ORDER BY b.hitCount DESC")
        List<BoardEntity> selectPopBoard(Pageable pageable);

        // 최신 게시글 3개 불러오기
        @Query("SELECT b FROM BoardEntity b ORDER BY b.createDate DESC")
        List<BoardEntity> selectRecentBoard(PageRequest pageRequest);

}
