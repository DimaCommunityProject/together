package net.dima_community.CommunityProject.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.port.VerifyRandomCodeHolder;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;
import net.dima_community.CommunityProject.member.domain.Member;
import net.dima_community.CommunityProject.member.service.MemberService;

@Controller
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailSender emailSender;
    private final VerifyRandomCodeHolder verifyRandomCodeHolder;
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/send")
    public boolean send(Member member) throws MessagingException {
        Member newMember = memberService.setEncodedPassword(member);
        String generatedString = verifyRandomCodeHolder.setRandomCode();
        // member 가저장..
        memberService.saveMemberWithVerificationCode(newMember, generatedString);
        Email email = Email.builder()
                .to(newMember.getMemberEmail())
                .title("디마 회원가입 본인인증")
                .content("인증번호는 " + generatedString + " 입니다.")
                .build();

        boolean result = emailSender.sendMail(email);
        return result;
    }

    @ResponseBody
    @GetMapping("/verifyCode")
    public boolean verifyCode(String to, String code) {
        boolean result = memberService.verifyMemberByCode(to, code);
        return result;
    }

    @ResponseBody
    @GetMapping("/approve")
    public boolean approve(String id, String to) {
        memberService.approve(id);
        Email email = Email.builder()
                .to(to)
                .title("디마 커뮤니티 회원가입 승인 메일입니다.")
                .content(id + "님의 회원가입이 승인되었습니다. 환영합니다!")
                .build();
        try {
            emailSender.sendMail(email);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @ResponseBody
    @GetMapping("/refuse")
    public boolean refuse(String id, String to) {
        Email email = Email.builder()
                .to(to)
                .title("디마 커뮤니티 회원가입 거절 메일입니다.")
                .content(id + "님의 회원가입이 거절되었습니다.\n" +
                        "디지털마스터 합격 메일을 다시 캡쳐하여 본 이메일 주소로 다시 보내주세요!")
                .build();

        try {
            emailSender.sendMail(email);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

}
