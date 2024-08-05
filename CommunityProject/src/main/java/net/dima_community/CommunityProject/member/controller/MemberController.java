package net.dima_community.CommunityProject.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.member.service.MemberService;

@Controller
@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {
    public final MemberService memberService;

    @GetMapping("/join")
    public String join() {
        return "user/join";
    }

    @GetMapping("/joinProc")
    public String joinProc() {

        return "main";
    }

    @GetMapping("checkDuplicate")
    public boolean checkDuplicate(@RequestParam(name = "memberId") String memberId) {
        boolean result = memberService.findByIdThroughConn(memberId);

        return result;
    }
}
