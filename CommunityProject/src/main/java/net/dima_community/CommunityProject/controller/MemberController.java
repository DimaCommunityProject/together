package net.dima_community.CommunityProject.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;
import net.dima_community.CommunityProject.service.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberservice;
	private final EmailSender emailSender;

	/**
	 * 회원가입을 위한 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("/member/join")
	public String join() {
		return "authentication-register2";
	}

	// ID 중복확인
	@GetMapping("checkDuplicate")
	public boolean checkDuplicate(@RequestParam(name = "memberId") String memberId) {
		boolean result = memberservice.findByIdThroughConn(memberId);

		return result;
	}

	// 로그인 화면 요청(사용자 이전 url 기억 필요)
	@GetMapping("/member/login")
	public String login(
			HttpServletRequest request, // 이전 url 가져오기
			@RequestParam(value = "error", required = false) String error, // 로그인 오류 시
			@RequestParam(value = "errMessage", required = false) String errMessage, // 에러메세지
			Model model) {

		model.addAttribute("error", error);
		model.addAttribute("errMessage", errMessage);

		return "member/login";
	}// end login

	// 사용자 아이디 찾기 화면 요청
	@GetMapping("/member/findId")
	public String findId(
			HttpServletRequest request, // 이전 url 가져오기
			Model model) {
		return "member/findId";
	}

	// 사용자 아이디 찾기 결과
	@PostMapping("/member/findIdResult")
	@ResponseBody
	public String findIdResult(
			HttpServletRequest request,
			@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail,
			Model model) {
		String result = memberservice.findmemId(memberName, memberEmail);
		log.info("사용자 아이디 찾기 결과 : {}", result);

		// if(result == null) {
		// model.addAttribute("bool", false);
		// model.addAttribute("message", "조회결과가 없습니다.");
		// }
		//
		// model.addAttribute("bool", true);
		// model.addAttribute("message", "회원님의 아이디는 " + result + "입니다.");

		return result;
	}// end findIdProc

	// 사용자 비밀번호 찾기 화면 요청
	@GetMapping("/member/findPw")
	public String findPw(
			HttpServletRequest request, // 이전 url 가져오기
			Model model) {
		return "member/findPw";
	}

	// 사용자 비밀번호 찾기 결과
	@PostMapping("/member/findPwResult")
	@ResponseBody
	public String findPwResult(
			HttpServletRequest request,
			@RequestParam("memberEmail") String memberEmail, @RequestParam("memberId") String memberId,
			@RequestParam("memberName") String memberName, MemberDTO memberDTO, Model model) {
		try {
			memberDTO.setMemberId(memberId);
			memberDTO.setMemberEmail(memberEmail);
			memberDTO.setMemberName(memberName);

			log.info("사용자 입력을 dto에 set함 : {}", memberDTO.toString());

			int search = memberservice.PwCheck(memberDTO); // 서비스 단에서 사용자 맞는지 확인

			if (search == 0) {
				// model.addAttribute("msg", "기입된 정보가 잘못되었습니다. 다시 입력해주세요.");
				// return "member/findPwResult";
				return "none";
			}

			// String newPw = RandomStringUtils.randomAlphanumeric(10); //대소문자, 숫자를 랜덤으로 생성
			// =>

			String newPw = new Random().ints(48, 123)
					.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
					.limit(20)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
					.toString();

			log.info("랜덤 문자열 : {}", newPw);

			memberDTO.setMemberPw(newPw); // 임시비번 넣고

			// 임시 비번을 이메일에 보내기 위해.
			Email email = Email.builder()
					.to(memberDTO.getMemberEmail())
					.title(memberDTO.getMemberName() + "님의 임시비밀번호 안내 이메일 입니다.")
					.content("인증번호는 " + newPw + " 입니다.")
					.build();

			boolean result = emailSender.sendMail(email);

			log.info("이메일이 갔나요? : {}", result);

			memberservice.setEncodedPassword(memberDTO); // 업뎃
			// model.addAttribute("newPw", newPw); //사용자에게 보여줌
			return newPw;

		} catch (Exception e) {
			e.printStackTrace();
			// model.addAttribute("msg", "오류가 발생되었습니다.");
			return "error";
		}
		// return "member/findPwResult";
	}// end findPwProc

	// 비밀번호 바꾸기 화면 요청
	@GetMapping("/member/changePw")
	public String changePw(
			HttpServletRequest request, @RequestParam("loginName") String memberId, Model model) {
		model.addAttribute("loginName", memberId);
		return "member/changePw";
	}// end changePw

	// 비밀번호 바꾸기
	@PostMapping("/member/changePw")
	@ResponseBody
	public Boolean chagePwck(
			HttpServletRequest request, @RequestParam("newmemberPw") String newmemberPw,
			@RequestParam("loginName") String memberId, MemberDTO memberDTO, Model model) {

		log.info("아이디 확인 : {}", memberId);
		log.info("새 비번 확인 : {}", newmemberPw);

		memberDTO.setMemberId(memberId);
		memberDTO.setMemberPw(newmemberPw);

		log.info("새 비번 DTO 확인 : {}", memberDTO.toString());

		try {
			memberservice.setEncodedPassword(memberDTO); // 비번 업뎃
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}// end class
