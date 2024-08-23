package net.dima_community.CommunityProject.dto.board;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.board.JobBoardEntity;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class JobBoardDTO {
    private Long jobBoardId;
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;

    public static JobBoardDTO toDTO(JobBoardEntity entity){
        return JobBoardDTO.builder()
                .jobBoardId(entity.getJobBoardId())
                .deadline(entity.getDeadline())
                .limitNumber(entity.getLimitNumber())
                .currentNumber(entity.getCurrentNumber())
                .build();
    }
    
}
