package net.dima_community.CommunityProject.dto.board;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.entity.board.JobBoardRecruitEntity;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class JobBoardRecruitDTO {
    private Long recruitId;
    private Long jobBoardId;
    private String memberId;
    private String memberGroup;
    private String memberPhone;
    private String memberEmail;

    public static JobBoardRecruitDTO toDTO (JobBoardRecruitEntity entity, Long jobBoardId, String memberId){
        return JobBoardRecruitDTO.builder()
            .recruitId(entity.getRecruitId())
            .jobBoardId(jobBoardId)
            .memberId(memberId)
            .memberGroup(entity.getMemberGroup())
            .memberPhone(entity.getMemberPhone())
            .memberEmail(entity.getMemberEmail())
            .build();
    }
}
