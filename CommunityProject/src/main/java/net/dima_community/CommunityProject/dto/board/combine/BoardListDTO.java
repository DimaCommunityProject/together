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

}
