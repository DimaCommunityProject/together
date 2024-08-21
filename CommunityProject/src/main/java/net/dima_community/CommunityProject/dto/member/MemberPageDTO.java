package net.dima_community.CommunityProject.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberPageDTO {

    public Long memberPageSeq;

    public String memberId;

    public String memberSelfInfo;

    public String memberInterest;

    public String memberSkill;

    public String memberGit;

    public String memberBlog;

    public String memberResume;

    public MemberPageDTO update(MemberPageDTO memberPage) {
        this.memberSelfInfo = memberPage.getMemberSelfInfo();
        this.memberInterest = memberPage.getMemberInterest();
        this.memberSkill = memberPage.getMemberSkill();
        this.memberGit = memberPage.getMemberGit();
        this.memberBlog = memberPage.getMemberBlog();
        this.memberResume = memberPage.getMemberResume();

        return this;
    }

    public MemberPageDTO updateSkill(String memberSkill) {
        this.memberSkill = memberSkill;
        return this;
    }
}
