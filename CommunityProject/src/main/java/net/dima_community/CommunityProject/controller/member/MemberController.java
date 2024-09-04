package net.dima_community.CommunityProject.controller.member;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.email.domain.Email;
import net.dima_community.CommunityProject.email.service.EmailSender;
import net.dima_community.CommunityProject.service.member.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberservice;
	private final EmailSender emailSender;

	// ===================== 회원가입 요청 페이지 =====================

	/**
	 * 회원가입을 위한 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("/member/join")
	public String join() {
		return "member/authentication-register2";
	}

	/**
	 * ID 중복확인
	 * 
	 * @param memberId
	 * @return
	 */
	@GetMapping("/member/checkDuplicate")
	@ResponseBody
	public boolean checkDuplicate(@RequestParam(name = "memberId") String memberId) {
		boolean result = memberservice.findByIdThroughConn(memberId);
		log.info("" + result);
		return result;
	}
	
	/**
	 * 이메일 중복확인 
	 * @param memberDTO
	 * @return
	 */
	@PostMapping("/register")
    public ResponseEntity<String> registerMember(@RequestBody MemberDTO memberDTO) {
        try {
        	memberservice.registerMember(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

	// ===================== 로그인 =====================

	/**
	 * 로그인 화면 요청(사용자 이전 url 기억 필요)
	 * 
	 * @param request
	 * @param error
	 * @param errMessage
	 * @param model
	 * @return
	 */
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

	// ===================== 아이디 찾기 =====================

	/**
	 * 사용자 아이디 찾기 화면 요청
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/member/findId")
	public String findId(
			HttpServletRequest request, // 이전 url 가져오기
			Model model) {
		return "member/findId";
	}// end findId

	/**
	 * 사용자 아이디 찾기 결과
	 * 
	 * @param request
	 * @param memberName
	 * @param memberEmail
	 * @param model
	 * @return
	 */
	@PostMapping("/member/findIdResult")
	@ResponseBody
	public String findIdResult(
			HttpServletRequest request,
			@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail,
			Model model) {

		String result = memberservice.findmemId(memberName, memberEmail);
		log.info("사용자 아이디 찾기 결과 : {}", result);

		return result;
	}// end findIdProc

	// ===================== 비밀번호 찾기 =====================

	/**
	 * 사용자 비밀번호 찾기 화면 요청
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping("/member/findPw")
	public String findPw(
			HttpServletRequest request, // 이전 url 가져오기
			Model model) {
		return "member/findPw";
	}

	/**
	 * 사용자 비밀번호 찾기 결과
	 * 
	 * @param request
	 * @param memberEmail
	 * @param memberId
	 * @param memberName
	 * @param memberDTO
	 * @param model
	 * @return
	 */
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
				return "none";
			}

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

			log.info("db에 업뎃하기 전 dto : {}", memberDTO.toString());
			memberservice.setEncodedPassword(memberDTO); // 업뎃

			boolean bool = memberservice.PwUpdate(memberDTO);

			// MemberEntity(memberId=가나다라마바사,
			// memberPw=$2a$10$J8BYtFobveWi3SXGZN8nI.dhkIIfWt.x1GiU5MrPOSBR8pTyYEnUC,
			// memberEnabled=null, memberRole=null, memberName=가나다라마바사,
			// memberEmail=rnrdudghg3122@gmail.com, memberGroup=null, memberPhone=null,
			// badge1=null, badge2=null, memberVerifyCode=null, memberPageEntity=null,
			// memberProjectEntity=null)
			// log.info("db에 업뎃하기 전 entity : {}", entity.toString());

			// memberRepository.save(entity);
			if (bool) {
				return newPw;
			} // 임시비번 잘 업뎃함
			else {
				return "false";
			} // 임시비번 업뎃 실패

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}// end findPwProc

	// ===================== 비밀번호 바꾸기 =====================

	/**
	 * 비밀번호 바꾸기 화면 요청
	 * 
	 * @param request
	 * @param memberId
	 * @param model
	 * @return
	 */
	@GetMapping("/member/changePw")
	public String changePw(
			HttpServletRequest request, @RequestParam("loginName") String memberId, Model model) {
		model.addAttribute("loginName", memberId);
		return "member/changePw";
	}// end changePw

	/**
	 * 비밀번호 바꾸기
	 * 
	 * @param request
	 * @param newmemberPw
	 * @param memberId
	 * @param memberDTO
	 * @param model
	 * @return
	 */
	@PostMapping("/member/changePw")
	@ResponseBody
	public String chagePwck(
			HttpServletRequest request, @RequestParam("newmemberPw") String newmemberPw,
			@RequestParam("loginName") String memberId, MemberDTO memberDTO, Model model) {

		log.info("아이디 확인 : {}", memberId);
		log.info("새 비번 확인 : {}", newmemberPw);

		memberDTO.setMemberId(memberId);
		memberDTO.setMemberPw(newmemberPw);

		log.info("새 비번 DTO 확인 : {}", memberDTO.toString());

		try {
			memberservice.setEncodedPassword(memberDTO); // 비번 암호화 후 dto 업뎃
			boolean bool = memberservice.PwUpdate(memberDTO); // dto를 레파지토리에서 db로 업뎃

			if (bool) {
				return "true";
			} else {
				return "false";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}// end chagePwck
}// end class
