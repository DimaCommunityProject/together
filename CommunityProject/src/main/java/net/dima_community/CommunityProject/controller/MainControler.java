package net.dima_community.CommunityProject.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.LoginMemberDetails;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.member.AdminNoteDTO;
import net.dima_community.CommunityProject.service.board.BoardService;
import net.dima_community.CommunityProject.service.main.MainService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainControler {
	private final MainService mainService;
	private final BoardService boardService;

	@GetMapping({ "", "/" })
	public String index(
			@AuthenticationPrincipal LoginMemberDetails loginUser, Model model) {
		if (loginUser != null)
			model.addAttribute("loginName", loginUser.getUsername());

		// 공지사항 불러오기
		List<AdminNoteDTO> noteList = mainService.selectNoteAll();

		// 인기게시글 불러오기
		List<BoardDTO> popList = boardService.selectPopBoard();

		// 최신게시글 불러오기
		List<BoardDTO> recentList = boardService.selectRecentBoard();

		model.addAttribute("noteList", noteList);
		model.addAttribute("popList", popList);
		model.addAttribute("recentList", recentList);

		return "/main/main";

	}
}
