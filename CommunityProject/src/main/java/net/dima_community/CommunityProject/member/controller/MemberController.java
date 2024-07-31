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
        return "member/join";
    }

    @PostMapping("/joinProc")
    public String joinProc(Member member) {

        return "main";
    }

    @GetMapping("checkDuplicate")
    public boolean checkDuplicate(String id) {
        try {
            memberService.findById(id);
        } catch (Exception e) {
            return true;// 사용가능한 id
        }
        return false;// 사용불가능한 id
    }
}
