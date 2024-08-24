package net.dima_community.CommunityProject.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "memberproject")
@Builder
public class MemberProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberproject_seq")
    private Long memberProjectSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "project_subtitle")
    private String projectSubtitle;

    @Column(name = "project_git")
    private String projectGit;

    public static MemberProjectEntity from(MemberProjectDTO memberProjectDTO, MemberEntity memberEntity) {
        return MemberProjectEntity.builder()
                .memberProjectSeq(memberProjectDTO.getMemberProjectSeq())
                .memberEntity(memberEntity)
                .projectTitle(memberProjectDTO.getProjectTitle())
                .projectSubtitle(memberProjectDTO.getProjectSubtitle())
                .projectGit(memberProjectDTO.getProjectGit())
                .build();
    }

    public MemberProjectDTO toModel() {
        return MemberProjectDTO.builder()
                .memberProjectSeq(memberProjectSeq)
                .memberId(memberEntity.getMemberId())
                .projectTitle(projectTitle)
                .projectSubtitle(projectSubtitle)
                .projectGit(projectGit)
                .build();
    }
}
