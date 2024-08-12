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
import net.dima_community.CommunityProject.domain.Email;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.service.EmailSenderService;
import net.dima_community.CommunityProject.service.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor	
public class MemberController {
	private final MemberService memberservive;
	private final EmailSenderService emailSender;
	
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
	
	//사용자 아이디 찾기 화면 요청
	@GetMapping("/member/findId")
	public String findId(
			HttpServletRequest request,	//이전 url 가져오기
			Model model
			) {
		return "member/findId";
	}
	
	//사용자 아이디 찾기
	@PostMapping("/member/findIdResult")
	public String findIdResult(
			HttpServletRequest request, 
			@RequestParam("memberName") String memberName,@RequestParam("memberEmail") String memberEmail, Model model
			) {
		String result = memberservive.findmemId(memberName, memberEmail);
		
		if(result == null) {
			model.addAttribute("msg", "조회결과가 없습니다.");
		}
		
		log.info("사용자 아이디 : {}", result);
		model.addAttribute("result", result);
		
		return "member/findIdResult";
	}//end findIdProc
	
	//사용자 비밀번호 찾기 화면 요청
	@GetMapping("/member/findPw")
	public String findPw(
			HttpServletRequest request,	//이전 url 가져오기
			Model model
			) {
		return "member/findPw";
	}
	
	//사용자 비밀번호 찾기
	@PostMapping("/member/findPwResult")
	public String findPwResult(
			HttpServletRequest request, 
			@RequestParam("memberEmail") String memberEmail, @RequestParam("memberId") String memberId, @RequestParam("memberName") String memberName
			, MemberDTO memberDTO, Model model
			){
		try {	
			memberDTO.setMemberId(memberId);
			memberDTO.setMemberEmail(memberEmail);
			memberDTO.setMemberName(memberName);
			
			log.info("사용자 입력을 dto에 set함 : {}", memberDTO.toString());
			
			int search = memberservive.PwCheck(memberDTO);	//서비스 단에서 사용자 맞는지 확인
			
			if(search == 0) {
				model.addAttribute("msg", "기입된 정보가 잘못되었습니다. 다시 입력해주세요.");
				return "member/findPwResult"; 
			}
			
			//String newPw = RandomStringUtils.randomAlphanumeric(10);	//대소문자, 숫자를 랜덤으로 생성 => 
			
			String newPw = new Random().ints(48, 123)
		            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
		            .limit(20)
		            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
		            .toString();
			
			log.info("랜덤 문자열 : {}", newPw);
			
			memberDTO.setMemberPw(newPw);								//임시비번 넣고
			
			//임시 비번을 이메일에 보내기 위해.
			Email email = Email.builder()
		               .to(memberDTO.getMemberEmail())
		               .title(memberDTO.getMemberName()+"님의 임시비밀번호 안내 이메일 입니다.")
		               .content("인증번호는 " + newPw + " 입니다.")
		               .build();

		    boolean result = emailSender.sendMail(email);
			
			log.info("이메일이 갔나요? : {}", result);
			
		    memberservive.PwUpdate(memberDTO);							//업뎃
			model.addAttribute("newPw", newPw);							//사용자에게 보여줌
			
		} catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("msg", "오류가 발생되었습니다.");
		}
		return "member/findPwResult";
	}//end findPwProc
}
