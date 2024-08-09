package net.dima_community.CommunityProject.entity.board;

import java.util.List;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.JobBoardDTO;


@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "job_board")
public class JobBoardEntity {
    
    @Id
    @Column(name = "board_id")
    private Long boardId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private BoardEntity boardEntity;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "limit_number")
    private int limitNumber;
    
    @Column(name = "current_number")
    private int currentNumber;

    // 자식
    @OneToMany(mappedBy = "jobBoardEntity", cascade = CascadeType.REMOVE, fetch=FetchType.LAZY, orphanRemoval = true)
    @OrderBy("board_id")
    private List<JobBoardRecruitEntity> JobBoardRecruitEntities;

    public static JobBoardEntity toEntity(JobBoardDTO dto, BoardEntity boardEntity) {
        return JobBoardEntity.builder()
                .boardId(dto.getBoardId())
                .boardEntity(boardEntity)
                .deadline(dto.getDeadline())
                .limitNumber(dto.getLimitNumber())
                .currentNumber(dto.getCurrentNumber())
                .build();
    }
}
