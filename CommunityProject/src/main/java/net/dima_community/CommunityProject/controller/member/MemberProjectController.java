package net.dima_community.CommunityProject.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.service.MemberService;
import net.dima_community.CommunityProject.service.member.MemberPageService;
import net.dima_community.CommunityProject.service.member.MemberProjectService;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/memberproject")
public class MemberProjectController {
    private final MemberService memberService;
    private final MemberProjectService memberProjectService;

    @PostMapping("/updateproject")
    @ResponseBody
    public boolean updateProject(
            @RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "projectSeq") Long projectSeq,
            @RequestParam(name = "projectTitle") String projectTitle,
            @RequestParam(name = "projectSubtitle") String projectSubtitle,
            @RequestParam(name = "projectGit") String projectGit) {
        MemberDTO memberDTO = memberService.findById(memberId);
        memberProjectService.updateProject(memberDTO, MemberProjectDTO.builder()
                .memberProjectSeq(projectSeq)
                .memberId(memberId)
                .projectTitle(projectTitle)
                .projectSubtitle(projectSubtitle)
                .projectGit(projectGit)
                .build());
        return true;
    }

    @PostMapping("/deleteproject")
    @ResponseBody
    public boolean deleteproject(@RequestParam(name = "projectSeq") Long projectSeq) {
        memberProjectService.deleteProject(projectSeq);
        return true;
    }

    @PostMapping("/savenewproject")
    @ResponseBody
    public boolean savenewproject(@RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "projectTitle") String projectTitle,
            @RequestParam(name = "projectSubtitle") String projectSubtitle,
            @RequestParam(name = "projectGit") String projectGit) {
        MemberDTO member = memberService.findById(memberId);
        log.info(member.toString());
        memberProjectService.save(member, MemberProjectDTO.builder()
                .projectTitle(projectTitle)
                .projectSubtitle(projectSubtitle)
                .projectGit(projectGit)
                .build());
        return true;
    }
}
