package net.dima_community.CommunityProject.member.domain;

import lombok.Builder;
import lombok.Getter;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;

@Getter
public class Member {

    public String memberId;

    public String memberPw;

    public String memberEnabled;

    public String memberRole;

    public String memberName;

    public String memberGroup;

    public String memberPhone;

    public String memberEmail;

    public String badge1;

    public String badge2;

    public String memberGit;

    public String memberBlog;

    public String memberResume;

    public String memberVerifyCode;

    @Builder
    public Member(String memberId, String memberPw, String memberEnabled, String memberRole, String memberName,
            String memberGroup, String memberPhone, String memberEmail,
            String badge1, String badge2, String memberGit, String memberBlog, String memberResume,
            String memberVerifyCode) {
        super();
        this.memberId = memberId;
        this.memberPw = memberPw;
        this.memberEnabled = memberEnabled;
        this.memberRole = memberRole;
        this.memberName = memberName;
        this.memberGroup = memberGroup;
        this.memberPhone = memberPhone;
        this.memberEmail = memberEmail;
        this.badge1 = badge1;
        this.badge2 = badge2;
        this.memberGit = memberGit;
        this.memberBlog = memberBlog;
        this.memberResume = memberResume;
        this.memberVerifyCode = memberVerifyCode;
    }

    public Member updateVerifyCode(String verifyCode) {
        this.memberVerifyCode = verifyCode;
        return this;
    }

    public Member setEncodedPassword(BCryptEncoderHolder bCryptEncoderHolder) {
        this.memberPw = bCryptEncoderHolder.encodedPassword(this.getMemberPw());
        return this;
    }

    public void enabledToYes() {
        this.memberEnabled = Character.toString('Y');
    };
}
