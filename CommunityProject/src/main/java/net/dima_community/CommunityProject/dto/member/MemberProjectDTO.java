package net.dima_community.CommunityProject.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProjectDTO {
    public Long memberProjectSeq;

    public String memberId;

    public String projectTitle;

    public String projectSubtitle;

    public String projectGit;

    public MemberProjectDTO update(MemberProjectDTO memberProject) {
        this.projectTitle = memberProject.getProjectTitle();
        this.projectSubtitle = memberProject.getProjectSubtitle();
        this.projectGit = memberProject.getProjectGit();
        return this;
    }
}
