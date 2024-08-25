package net.dima_community.CommunityProject.dto.board;

import java.util.List;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ReplyDTO {
    private Long replyId;
    private Long boardId;
    private Long parentReplyId;
    private String memberId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Integer likeCount;

    // 로그인한 사용자의 댓글 좋아요 여부 정보를 담기 위한 변수
    private boolean likeByUser;

    // 대댓글 리스트
    private List<ReplyDTO> childReplies;

    public static ReplyDTO toDTO(ReplyEntity entity, Long boardId, String memberId) {
        return ReplyDTO.builder()
                .replyId(entity.getReplyId())
                .boardId(boardId)
                .parentReplyId(entity.getParentReplyId())
                .memberId(memberId)
                .content(entity.getContent())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .likeCount(entity.getLikeCount())
                .build();
    }

}
