package net.dima_community.CommunityProject.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.port.VerifyRandomCodeHolder;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;
import net.dima_community.CommunityProject.service.member.MemberService;
import net.dima_community.CommunityProject.service.member.MemberVerifyCodeService;

@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailSender emailSender;
    private final VerifyRandomCodeHolder verifyRandomCodeHolder;
    private final MemberService memberService;
    private final MemberVerifyCodeService memberVerifyCodeService;

    @ResponseBody
    @PostMapping("/send")
    public boolean send(@RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "memberEmail") String memberEmail) {
                log.info("==================== 지금 이메일 보내기 요청 컨트롤러야==================");
        String generatedString = verifyRandomCodeHolder.setRandomCode();
        memberVerifyCodeService.insert(memberId, generatedString);
        Email email = Email.builder()
                .to(memberEmail)
                .title("디마 회원가입 본인인증")
                .content("인증번호는 " + generatedString + " 입니다.")
                .build();

        boolean result = emailSender.sendMail(email);
        return result;
    }

    @ResponseBody
    @GetMapping("/verifyCode")
    public boolean verifyCode(@RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "memberVerifyCode") String code) {
        boolean result = memberVerifyCodeService.verifyMemberByCode(memberId, code);
        log.info("" + result);
        return result;
    }

    // @ResponseBody
    @GetMapping("/approve")
    public String approve(
            @RequestParam(name = "memberId") String id, @RequestParam(name = "memberEmail") String to, Model model) {

        memberService.approve(id);
        Email email = Email.builder()
                .to(to)
                .title("디마 커뮤니티 회원가입 승인 메일입니다.")
                .content(id + "님의 회원가입이 승인되었습니다. 환영합니다!")
                .build();
        try {
            emailSender.sendMail(email);
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errMessage", "승인을 처리하지 못했습니다.");
            return "admin/adminPage"; // 원래 페이지로 돌아가면서 오류 메시지 전달
        }
        return "redirect:/admin/adminPage";
    }

    // @ResponseBody
    @GetMapping("/refuse")
    public String refuse(@RequestParam(name = "memberId") String id, @RequestParam(name = "memberEmail") String to,
            Model model) {
        Email email = Email.builder()
                .to(to)
                .title("디마 커뮤니티 회원가입 거절 메일입니다.")
                .content(id + "님의 회원가입이 거절되었습니다.\n" +
                        "디지털마스터 합격 메일을 다시 캡쳐하여 본 이메일 주소로 다시 보내주세요!")
                .build();

        try {
            emailSender.sendMail(email);
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("errMessage", "승인을 처리하지 못했습니다.");
            return "admin/adminPage"; // 원래 페이지로 돌아가면서 오류 메시지 전달
        }
        return "redirect:/admin/adminPage";

    }

    @PostMapping("/resend")
    @ResponseBody
    public boolean resend(@RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "memberEmail") String newEmail) {

        String generatedString = verifyRandomCodeHolder.setRandomCode();
        // verifyCode 업데이트
        memberVerifyCodeService.insert(memberId, generatedString);
        log.info("service 완료");
        Email email = Email.builder()
                .to(newEmail)
                .title("디마 커뮤니티 이메일 재설정 인증번호입니다")
                .content("인증번호는 " + generatedString + " 입니다.")
                .build();

        boolean result = emailSender.sendMail(email);
        return result;
    }

}
