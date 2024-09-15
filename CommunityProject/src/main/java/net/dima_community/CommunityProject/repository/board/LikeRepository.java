package net.dima_community.CommunityProject.repository.board;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.LikeEntity;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;
import net.dima_community.CommunityProject.entity.member.MemberEntity;



public interface LikeRepository extends JpaRepository<LikeEntity, Long>{

        // boardId에 해당하는 좋아요 수를 조회하는 쿼리
        @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.boardEntity = :boardEntity")
        long countByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

        // 특정 회원이 특정 게시글에 좋아요를 눌렀는지 확인하는 쿼리
        @Query("SELECT l FROM LikeEntity l "+
                "WHERE l.memberEntity = :memberEntity AND l.boardEntity = :boardEntity")
        Optional<LikeEntity> findByMemberAndBoard(@Param("memberEntity") MemberEntity memberEntity, @Param("boardEntity") BoardEntity boardEntity);
                
        // 특정 댓글에서 특정 회원이 좋아요를 눌렀는지 확인하는 쿼리
        @Query("SELECT COUNT(l) > 0 FROM LikeEntity l "+
                "WHERE l.replyEntity.replyId = :replyId AND l.memberEntity.memberId = :memberId")
        boolean existsByReplyIdAndMemberId(@Param("replyId")Long replyId, @Param("memberId") String memberId);


        // 특정 회원이 특정 댯글에 좋아요를 눌렀는지 확인하는 쿼리
        @Query("SELECT l FROM LikeEntity l "+
                "WHERE l.memberEntity = :memberEntity AND l.replyEntity = :replyEntity")
        Optional<LikeEntity> findByMemberAndReply(@Param("memberEntity") MemberEntity memberEntity, @Param("replyEntity") ReplyEntity replyEntity);


}
