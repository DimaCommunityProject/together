package net.dima_community.CommunityProject.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.member.domain.Member;

@Entity
@Table(name = "memberproject")
@Builder
public class MemberProjectEntity {

    // @SequenceGenerator(name = "memberproject_seq", sequenceName =
    // "memberproject_seq", initialValue = 1, allocationSize = 1)

    @Id
    // @GeneratedValue(generator = "memberproject_seq")
    @Column(name = "member_project_seq")
    private Long memberProjectSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "project_title")
    private String projectSubtitle;

    @Column(name = "project_git")
    private String projectGit;

    public static MemberProjectEntity from(MemberProjectDTO memberProjectDTO, Member member) {
        return MemberProjectEntity.builder()
                .memberProjectSeq(memberProjectDTO.getMemberProjectSeq())
                .member(member)
                .projectTitle(memberProjectDTO.getProjectTitle())
                .projectSubtitle(memberProjectDTO.getProjectSubtitle())
                .projectGit(memberProjectDTO.getProjectGit())
                .build();
    }

    public MemberProjectDTO toModel() {
        return MemberProjectDTO.builder()
                .memberProjectSeq(memberProjectSeq)
                .memberId(member.getMemberId())
                .projectTitle(projectTitle)
                .projectSubtitle(projectSubtitle)
                .projectGit(projectGit)
                .build();
    }
}
