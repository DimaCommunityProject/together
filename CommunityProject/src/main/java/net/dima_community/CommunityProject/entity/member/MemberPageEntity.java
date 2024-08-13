package net.dima_community.CommunityProject.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.member.infra.MemberEntity;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "memberpage")
public class MemberPageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_page_seq")
    private Long memberPageSeq;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "member_selfinfo")
    private String memberSelfInfo;

    @Column(name = "member_interest")
    private String memberInterest;

    @Column(name = "member_skill")
    private String memberSkill;

    @Column(name = "member_git")
    private String memberGit;

    @Column(name = "member_blog")
    private String memberBlog;

    @Column(name = "member_resume")
    private String memberResume;

    public static MemberPageEntity from(MemberPageDTO memberPage, MemberEntity memberEntity) {
        return MemberPageEntity.builder()
                .memberPageSeq(memberPage.getMemberPageSeq())
                .memberEntity(memberEntity)
                .memberSelfInfo(memberPage.getMemberSelfInfo())
                .memberInterest(memberPage.getMemberInterest())
                .memberSkill(memberPage.getMemberSkill())
                .memberGit(memberPage.getMemberGit())
                .memberBlog(memberPage.getMemberBlog())
                .memberResume(memberPage.getMemberResume())
                .build();
    }

    public MemberPageDTO toModel() {
        return MemberPageDTO.builder()
                .memberPageSeq(memberPageSeq)
                .memberId(memberEntity.getMemberId())
                .memberSelfInfo(memberSelfInfo)
                .memberInterest(memberInterest)
                .memberSkill(memberSkill)
                .memberGit(memberGit)
                .memberBlog(memberBlog)
                .memberResume(memberResume)
                .build();
    }
}
