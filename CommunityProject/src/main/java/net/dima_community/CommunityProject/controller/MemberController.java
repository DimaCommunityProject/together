package net.dima_community.CommunityProject.controller;

//import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor	
public class MemberController {
	private final MemberService memberservive;
	
	/**
	 * 회원가입을 위한 화면 요청
	 * @return
	 */
	@GetMapping("/member/join")
	public String join() {
		return "member/join";
	}
	
	// 회원 저장
	@PostMapping("/member/joinProc")
	public String joinProc(@ModelAttribute MemberDTO memberDTO) {
		log.info("회원 저장 : {}", memberDTO);
		
		// 롤, 계정 활성화 여부 세팅(관리자 계정은 sql에서 바로 admin으로 입력하면 될 듯)
		memberDTO.setMemberRole("ROLE_USER");	//관리자인지 그냥 사용자인지 정함. 관리자면 ROLE_ADMIN. 
		memberDTO.setMemberEnabled(true);	    //계정 활성화 여부. 이메일 인증이 완료되지 않으면 false로 해놓기
		memberDTO.setMemberGroup("3기");
		memberDTO.setMemberPhone("010-1111-1234");
		
		memberservive.joinProc(memberDTO);
		return "redirect:/";
	}//end joinproc
	
	@PostMapping("/member/confirmId")
	@ResponseBody
	public Boolean confirmId (
			@RequestParam(value="memberId") String memberId) {
		log.info("유저아이디 넘어왔슈 : {}", memberId);
		
		Boolean bool = memberservive.findId(memberId);
		log.info("무슨 타입? : {}", bool);
		
		return bool;
	}
	
	//로그인 화면 요청(사용자 이전 url 기억 필요)
	@GetMapping("/member/login")
	public String login(
			HttpServletRequest request,	//이전 url 가져오기
			@RequestParam(value="error", required=false) String error,				//로그인 오류 시 
			@RequestParam(value="errMessage", required=false) String errMessage,	//에러메세지
			Model model
			) {
		
		model.addAttribute("error", error);
		model.addAttribute("errMessage", errMessage);
		
		return "member/login";
	}//end login
	
//	//사용자 아이디 찾기 화면 요청
//	@GetMapping("/member/findId")
//	public String findId(
//			HttpServletRequest request,	//이전 url 가져오기
//			Model model
//			) {
//		return "member/findId";
//	}
//	
//	//사용자 아이디 찾기
//	@PostMapping("/member/findId")
//	@ResponseBody
//	public String findIdProc(
//			HttpServletRequest request, 
//			@RequestParam("memberName") String memberName,@RequestParam("memberEmail") String memberEmail
//			) {
//		String result = memberservive.findmemId(memberName, memberEmail);
//		
//		return result;
//	}//end findIdProc
//	
//	//사용자 비밀번호 찾기 화면 요청
//	@GetMapping("/member/findPw")
//	public String findPw(
//			HttpServletRequest request,	//이전 url 가져오기
//			Model model
//			) {
//		return "member/findPw";
//	}
//	
//	//사용자 비밀번호 찾기
//	@PostMapping("/member/findPwResult")
//	@ResponseBody
//	public String findPwProc(
//			HttpServletRequest request, 
//			@RequestParam("memberName") String memberName, @RequestParam("memberEmail") String memberEmail, @RequestParam("memberId") String memberId
//			, MemberDTO memberDTO, Model model
//			){
//		try {
//			memberDTO.setMemberId(memberId);
//			memberDTO.setMemberName(memberName);
//			memberDTO.setMemberEmail(memberEmail);
//			
//			log.info("사용자 입력을 dto에 set함 : {}", memberDTO.toString());
//			
//			int search = memberservive.PwCheck(memberDTO);	//서비스 단에서 dto로 사용자 맞는지 확인
//			
//			if(search != 1) {
//				model.addAttribute("msg", "기입된 정보가 잘못되었습니다. 다시 입력해주세요.");
//			}
//			
//			String newPw = RandomStringUtils.randomAlphanumeric(10);	//대소문자, 숫자를 랜덤으로 생성
//			memberDTO.setMemberPw(newPw);								//임시비번 넣고
//			memberservive.PwUpdate(memberDTO);							//업뎃
//			model.addAttribute("newPw", newPw);							//사용자에게 보여줌
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//			model.addAttribute("msg", "오류가 발생되었습니다.");
//		}
//		return "member/findPwResult";
//	}//end findPwProc
}
