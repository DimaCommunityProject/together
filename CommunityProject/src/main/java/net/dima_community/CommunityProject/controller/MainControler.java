package net.dima_community.CommunityProject.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.LoginMemberDetails;

@Controller
@Slf4j
public class MainControler {
	
	@GetMapping({"","/"})
	public String index(
			@AuthenticationPrincipal LoginMemberDetails loginUser
			, Model model
			) {
		if(loginUser != null)
			model.addAttribute("loginName", loginUser.getUsername());
		return "/member/index";

	}
}




