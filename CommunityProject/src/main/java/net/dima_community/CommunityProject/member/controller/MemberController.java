package net.dima_community.CommunityProject.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.MemberService;

@RequestMapping("member")
@RequiredArgsConstructor
public class MemberController {
    public final MemberService memberService;

    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @GetMapping("/joinProc")
    public String joinProc() {

        return "main";
    }

    @GetMapping("checkDuplicate")
    public boolean checkDuplicate(String id) {
        boolean result = memberService.findByIdThroughConn(id);

        return result;
    }
}
