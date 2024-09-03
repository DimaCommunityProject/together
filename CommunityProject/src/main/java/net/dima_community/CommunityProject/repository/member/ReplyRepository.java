package net.dima_community.CommunityProject.repository.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

    // boardEntity에 해당하는 댓글의 개수를 반환하는 메서드
    @Query("SELECT COUNT(r) FROM ReplyEntity r WHERE r.boardEntity = :boardEntity")
    long countByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

    // boardEntity에 해당하는 댓글들을 반환하는 메서드
    @Query("SELECT r FROM ReplyEntity r " +
            "WHERE r.boardEntity =: boardEntity " +
            "ORDER BY r.createDate DESC")
    List<ReplyEntity> findByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

    @Query("SELECT r FROM ReplyEntity r WHERE r.memberEntity = :memberEntity")
    List<ReplyEntity> findByMemberId(@Param("memberEntity") MemberEntity memberEntity);
}
