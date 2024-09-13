package net.dima_community.CommunityProject.dto.board.combine;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private int replyCount;
    private LocalDateTime createDate;
    // activity / recruit 
    private LocalDateTime deadline;
    private int dDay;
    private int limitNumber;
    private int currentNumber;

    public BoardListDTO(Long boardId, String memberId, String memberGroup, String title, int hitCount, int likeCount, int replyCount, LocalDateTime createDate, LocalDateTime deadline, int limitNumber, int currentNumber) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.memberGroup = memberGroup;
        this.title = title;
        this.hitCount = hitCount;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.createDate = createDate;
        this.deadline = deadline;
        this.limitNumber = limitNumber;
        this.currentNumber = currentNumber;

        updateDDay(); // dDay 값을 계산하여 설정
    }

    /**
     * D-Day 계산 함수
     */
    private void updateDDay() {
        if (this.deadline != null) {
            LocalDateTime now = LocalDateTime.now();
            this.dDay = (int) ChronoUnit.DAYS.between(now.toLocalDate(), this.deadline.toLocalDate());
        } else {
            this.dDay = -10000; // 또는 다른 기본값
        }
    }

}
