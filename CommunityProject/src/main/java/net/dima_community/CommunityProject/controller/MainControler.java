package net.dima_community.CommunityProject.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.LoginMemberDetails;
import net.dima_community.CommunityProject.dto.member.AdminNoteDTO;
import net.dima_community.CommunityProject.service.main.MainService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainControler {
	private final MainService mainService;

	@GetMapping({ "", "/" })
	public String index(
	        @AuthenticationPrincipal LoginMemberDetails loginUser, Model model) {
	    if (loginUser != null) {
	    	model.addAttribute("loginId", loginUser.getUsername()); // id
	        model.addAttribute("loginName", loginUser.getMemberName()); // 이름 추가
	        model.addAttribute("loginGroup", loginUser.getMemberGroup()); // 그룹 추가
	        model.addAttribute("loginEmail", loginUser.getMemberEmail()); // 이메일 추가
	    }

	    // 공지사항 불러오기
	    List<AdminNoteDTO> dtoList = mainService.selectNoteAll();
	    log.info("메인페이지 공지사항 dto: {}", dtoList.toString());

<<<<<<< HEAD
		log.info("메인페이지 공시항dto : {}", dtoList.toString());

		model.addAttribute("list", dtoList);

		return "main/main";
=======
	    model.addAttribute("list", dtoList);
>>>>>>> 527e81414b8e862e7cac047b8353e26a7292cee7

	    return "main/main";
	}
}
