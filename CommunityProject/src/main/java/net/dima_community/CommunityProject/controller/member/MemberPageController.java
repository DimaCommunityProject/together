package net.dima_community.CommunityProject.controller.member;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.MemberService;
import net.dima_community.CommunityProject.service.member.MemberPageService;
import net.dima_community.CommunityProject.service.member.MemberProjectService;

@Controller
@RequestMapping("/memberpage")
@RequiredArgsConstructor
public class MemberPageController {
    public final MemberService memberService;
    public final MemberPageService memberPageService;
    public final MemberProjectService memberProjectService;

    @GetMapping("/showpage")
    public String showpage() {
        // 로그인한 유저 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        Member member = memberService.findById(username);
        MemberPageDTO memberPage = memberPageService.findByUsername(member);
        MemberProjectDTO memberProject = memberProjectService.findByUsername(member);
        return "member/MemberPage";
    }
}
