package net.dima_community.CommunityProject.entity.board;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.JobBoardRecruitDTO;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "job_board_recruit")
public class JobBoardRecruitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id")
    private Long recruitId;

    // FK (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_board_id")
    private JobBoardEntity jobBoardEntity;
    
    // FK (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "member_group", nullable = false)
    private String memberGroup;

    @Column(name = "member_phone")
    private String memberPhone;
    
    @Column(name = "member_email")
    private String memberEmail;

    public static JobBoardRecruitEntity toEntity (JobBoardRecruitDTO dto, JobBoardEntity jobBoardEntity, MemberEntity memberEntity){
        return JobBoardRecruitEntity.builder()
            .recruitId(dto.getRecruitId())
            .jobBoardEntity(jobBoardEntity)
            .memberEntity(memberEntity)
            .memberGroup(dto.getMemberGroup())
            .memberPhone(dto.getMemberPhone())
            .memberEmail(dto.getMemberEmail())
            .build();
    }
}
