package net.dima_community.CommunityProject.repository.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;
import net.dima_community.CommunityProject.entity.member.MemberEntity;


public interface ReplyRepository extends JpaRepository<ReplyEntity,Long>{

    //Sehyun & Minseo
    // 전달받은 member가 작성한 댓글들 반환
    @Query("SELECT r FROM ReplyEntity r WHERE r.memberEntity = :memberEntity")
    List<ReplyEntity> findByMemberId(@Param("memberEntity") MemberEntity memberEntity);

    // boardEntity에 해당하는 댓글의 개수를 반환하는 메서드
    @Query("SELECT COUNT(r) FROM ReplyEntity r WHERE r.boardEntity = :boardEntity")
    long countByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

    // boardEntity에 해당하는 댓글들을 반환하는 메서드
    @Query("SELECT r FROM ReplyEntity r " +
            "WHERE r.boardEntity = :boardEntity " +
            "ORDER BY r.createDate DESC")
    List<ReplyEntity> findByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

    // 1. replyId와 동일한 parentReplyId 데이터의 개수를 반환하는 메서드
    @Query("SELECT COUNT(r) FROM ReplyEntity r WHERE r.parentReplyId = :replyId")
    int countByReplyIdInParent(@Param("replyId") Long replyId);

    // 2. replyId와 동일한 parentReplyId 데이터를 삭제하는 메서드
    @Modifying
    @Transactional
    @Query("DELETE FROM ReplyEntity r WHERE r.parentReplyId = :replyId")
    void deleteByParentReplyId(@Param("replyId") Long replyId);
}
