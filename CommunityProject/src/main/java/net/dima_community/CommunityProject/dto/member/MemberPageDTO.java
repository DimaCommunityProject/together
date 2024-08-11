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

    public String memberInterst;

    public String memberSkill;

    public String memberGit;

    public String memberBlog;

    public String memberResume;
}
