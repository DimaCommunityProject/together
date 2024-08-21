package net.dima_community.CommunityProject.controller.member;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.dto.member.MemberProjectDTO;
import net.dima_community.CommunityProject.service.MemberService;
import net.dima_community.CommunityProject.service.member.MemberPageService;
import net.dima_community.CommunityProject.service.member.MemberProjectService;

@Controller
@RequestMapping("/memberpage")
@RequiredArgsConstructor
@Slf4j
public class MemberPageController {
    public final MemberService memberService;
    public final MemberPageService memberPageService;
    public final MemberProjectService memberProjectService;

    @GetMapping("/showpage")
    public String showpage(@RequestParam(name = "memberId") String memberId, Model model) {
        // 로그인한 유저 정보 가져오기
        // Object principal =
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // String username = ((UserDetails) principal).getUsername();

        // 회원, 회원페이지, 회원프로젝트 객체 가져오기
        MemberDTO member = memberService.findById(memberId);
        MemberPageDTO memberPage = memberPageService.findByUsername(member.getMemberId());
        List<MemberProjectDTO> memberProject = memberProjectService.findByUsername(member.getMemberId());

        model.addAttribute("member", member);
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("memberProject", memberProject);
        return "member/memberPage";
    }

    @GetMapping("/updatepage")
    public String updatepage1(@RequestParam(name = "memberId") String memberId, Model model) {

        // 회원, 회원페이지, 회원프로젝트 객체 가져오기
        MemberDTO member = memberService.findById(memberId);
        MemberPageDTO memberPage = memberPageService.findByUsername(member.getMemberId());
        List<MemberProjectDTO> memberProject = memberProjectService.findByUsername(member.getMemberId());

        model.addAttribute("member", member);
        model.addAttribute("memberPage", memberPage);
        model.addAttribute("memberProject", memberProject);
        return "member/updatePage";
    }

    @PostMapping("/updatepage")
    public String updatepage2(@RequestParam String memberId, @RequestParam String memberName,
            @RequestParam String memberEmail, @RequestParam MemberPageDTO memberPage,
            @RequestParam MemberProjectDTO memberProject, Model model) {

        MemberDTO updatedMember = memberService.updateMember(memberId, memberName, memberEmail);
        MemberPageDTO updatedMemberPage = memberPageService.updatePage(updatedMember, memberPage);
        MemberProjectDTO updatedMemberProject = memberProjectService.updateProject(updatedMember, memberProject);

        return null;
        // return "redirect:/memberpage/showpage";
    }

    @PostMapping("/updateMemberSkill")
    @ResponseBody
    public boolean updateMemberSkill(@RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "memberSkill") String memberSkill) {
        boolean result = memberPageService.updateMemberSkill(memberId, memberSkill);
        return result;
    }
}
