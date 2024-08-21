package net.dima_community.CommunityProject.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.port.VerifyRandomCodeHolder;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;
import net.dima_community.CommunityProject.service.MemberService;

@Controller
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailSender emailSender;
    private final VerifyRandomCodeHolder verifyRandomCodeHolder;
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/send")
    public boolean send(@RequestBody MemberDTO member) throws MessagingException {
        MemberDTO newMember = memberService.setEncodedPassword(member);
        String generatedString = verifyRandomCodeHolder.setRandomCode();
        log.info(newMember.toString());

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
    public boolean verifyCode(@RequestParam(name = "memberEmail") String to,
            @RequestParam(name = "memberVerifyCode") String code) {
        boolean result = memberService.verifyMemberByCode(to, code);
        return result;
    }

    //@ResponseBody
    @GetMapping("/approve")
    public String approve(
    		@RequestParam(name="memberId") String id, @RequestParam(name="memberEmail") String to
    		, Model model) {
    	
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
            return "admin/adminPage";  // 원래 페이지로 돌아가면서 오류 메시지 전달
        }
        return "redirect:/admin/adminPage";
    }

    //@ResponseBody
    @GetMapping("/refuse")
    public String refuse(@RequestParam(name="memberId") String id, @RequestParam(name="memberEmail") String to
    		, Model model) {
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
            return "admin/adminPage";  // 원래 페이지로 돌아가면서 오류 메시지 전달
        }
        return "redirect:/admin/adminPage";

    }

}
