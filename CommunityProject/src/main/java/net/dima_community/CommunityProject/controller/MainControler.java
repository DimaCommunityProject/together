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
			model.addAttribute("loginId", loginUser.getUsername());
		
			// == name, group, email 가져오기 - 인영 == 
			model.addAttribute("loginName", loginUser.getMemberName()); // 이름 추가
	        model.addAttribute("loginGroup", loginUser.getMemberGroup()); // 그룹 추가
	        model.addAttribute("loginEmail", loginUser.getMemberEmail()); // 이메일 추가 
			// =======================================
		} else {
	        // In case loginUser is null, you can log a message or take another action
	        log.warn("No authenticated user found.");
	    }

		// 공지사항 불러오기
		List<AdminNoteDTO> dtoList = mainService.selectNoteAll();

		log.info("메인페이지 공시항dto : {}", dtoList.toString());

		model.addAttribute("list", dtoList);

		return "main/main";
		

	}
}
