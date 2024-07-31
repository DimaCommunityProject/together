package net.dima_community.CommunityProject.member.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dima_community.CommunityProject.member.domain.Member;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class MemberEntity {

    @Id
    @Column(name = "member_id")
    private String memberId;
    @Column(name = "member_pw")
    private String memberPw;
    @Column(name = "member_enabled")
    private String memberEnabled;
    @Column(name = "member_role")
    private String memberRole;
    @Column(name = "member_name")
    private String memberName;
    @Column(name = "member_group")
    private String memberGroup;
    @Column(name = "member_phone")
    private String memberPhone;
    @Column(name = "member_email")
    private String memberEmail;

    private String badge1;
    private String badge2;

    @Column(name = "member_git")
    private String memberGit;
    @Column(name = "member_blog")
    private String memberBlog;
    @Column(name = "member_resume")
    private String memberResume;
    @Column(name = "member_verify_code")
    private String memberVerifyCode;

    public static MemberEntity from(Member member) {
        return MemberEntity.builder()
                .memberId(member.getMemberId())
                .memberPw(member.getMemberPw())
                .memberEnabled(member.getMemberEnabled())
                .memberRole(member.getMemberRole())
                .memberName(member.getMemberName())
                .memberGroup(member.getMemberGroup())
                .memberPhone(member.getMemberPhone())
                .memberEmail(member.getMemberEmail())
                .badge1(member.getBadge1())
                .badge2(member.getBadge2())
                .memberGit(member.getMemberGit())
                .memberBlog(member.getMemberBlog())
                .memberResume(member.getMemberResume())
                .memberVerifyCode(member.getMemberVerifyCode())
                .build();
    }

    public Member toModel() {
        return Member.builder()
                .memberId(memberId)
                .memberPw(memberPw)
                .memberEnabled(memberEnabled)
                .memberRole(memberRole)
                .memberName(memberName)
                .memberGroup(memberGroup)
                .memberPhone(memberPhone)
                .memberEmail(memberEmail)
                .badge1(badge1)
                .badge2(badge2)
                .memberGit(memberGit)
                .memberBlog(memberBlog)
                .memberResume(memberResume)
                .memberVerifyCode(memberVerifyCode)
                .build();
    }
}
