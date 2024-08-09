package net.dima_community.CommunityProject.repository.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.dto.board.combine.BoardListDTO;
import net.dima_community.CommunityProject.entity.board.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

        // 카테고리 가 group이고, 전달받은 memberGroup에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
        @Query("SELECT b FROM BoardEntity b WHERE " +
                "b.category = 'group' AND " +
                "b.memberGroup =: userGroup AND " +
                "b.reported = false AND " +
                "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
        Page<BoardEntity> findByMemberGroupAndNotReportedAndTitleContaining(@Param("userGroup") String userGroup, @Param("searchWord") String searchWord, Pageable pageRequest);

        // 기수별 게시판 목록 (반환 타입 : BoardListDTO)
        @Query("SELECT new com.example.community.dto.display.BoardListDTO(" +
                "b.boardId, " +
                "b.memberEntity.memberId, " +
                "b.memberGroup, " +
                "b.title, " +
                "b.hitCount, " +
                "b.likeCount, " +
                "b.createDate, " +
                "NULL, " +  // deadline
                "0, " +      // limitNumber
                "0) " +      // currentNumber
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
        @Query("SELECT new com.example.community.dto.display.BoardListDTO(" +
                "b.boardId, " +
                "b.memberEntity.memberId, " +
                "b.memberGroup, " +
                "b.title, " +
                "b.hitCount, " +
                "b.likeCount, " +
                "b.createDate, " +
                "NULL, " +  // deadline
                "0, " +      // limitNumber
                "0) " +      // currentNumber
                "FROM BoardEntity b " +
                "WHERE b.category = :category " +
                "AND b.reported = false " +
                "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByCategoryAndTitleContaining(@Param("category") BoardCategory category, 
                                                                        @Param("searchWord") String searchWord, 
                                                                        Pageable pageRequest);


}
