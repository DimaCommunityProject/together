package net.dima_community.CommunityProject.dto.board.combine;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.JobBoardEntity;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BoardListDTO { // 게시물 목록 화면에 필요한 속성들만 모아 놓은 DTO
    private Long boardId;
    private String memberId;
    private String memberGroup;
    private String title;
    private int hitCount;
    private int likeCount;
    private LocalDateTime createDate;
    // activity / recruit 
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;

    /**
     * BoardEntity를 BoardListDTO로 변환하는 함수 (deadline : null, limitNumber : 0, currentNumber : 0)
     * @param entity
     * @return
     */
    public static BoardListDTO convertToDTO (BoardEntity entity){
        return BoardListDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(entity.getMemberEntity().getMemberId())
                .memberGroup(entity.getMemberGroup())
                .title(entity.getTitle())
                .hitCount(entity.getHitCount())
                .likeCount(entity.getLikeCount())
                .createDate(entity.getCreateDate())
                .deadline(null)
                .limitNumber(0)
                .currentNumber(0)
                .build();
    }

    /**
     * JobBoardEntity를 BoardListDTO로 변환하는 함수
     * @param entity
     * @return
     */
    public static BoardListDTO convertToDTO (JobBoardEntity entity){
        return BoardListDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(entity.getBoardEntity().getMemberEntity().getMemberId())
                .memberGroup(entity.getBoardEntity().getMemberGroup())
                .title(entity.getBoardEntity().getTitle())
                .hitCount(entity.getBoardEntity().getHitCount())
                .likeCount(entity.getBoardEntity().getLikeCount())
                .createDate(entity.getBoardEntity().getCreateDate())
                .deadline(entity.getDeadline())
                .limitNumber(entity.getLimitNumber())
                .currentNumber(entity.getCurrentNumber())
                .build();
    }


}
