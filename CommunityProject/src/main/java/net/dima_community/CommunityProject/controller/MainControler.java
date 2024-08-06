package net.dima_community.CommunityProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainControler {
	
	@GetMapping({"", "/"})
	public String index(
			Model model) {
		return "index";
	}
}
