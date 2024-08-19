package net.dima_community.CommunityProject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.util.PageNavigator;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.service.AdminService;


@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminservice;
	
	@Value("${admin.page.pageLimit}")
	int pageLimit;	//한페이지에 보여줄 글의 개수
	
	// 관리자 페이지 요청
	@GetMapping("/admin/adminPage")
	public String adminPage(
			@PageableDefault(page=1) Pageable pageable	//Pageable : 페이징을 해주는 객체.
			, Model model, 
			@RequestParam(name="memberGroup", defaultValue="1기") String memberGroup
			) {
		log.info("페이지이이이이이이 : {}", pageable);
		log.info("페이지이이이이이이 : {}", memberGroup);
		Page<MemberDTO> enabledtoList = adminservice.selectEnableAll(pageable, memberGroup);
		
		//페이지 번호 자동계산 해주는 PageNavigator 객체 생성
		int totalPages = (int)enabledtoList.getTotalPages();
		int page = pageable.getPageNumber();
		PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);
		
		//승인 안된 회원 불러오기
		List<MemberDTO> disabledtoList = adminservice.selectDisableAll();
		
		model.addAttribute("enablelist", enabledtoList);
		model.addAttribute("navi", navi);
		model.addAttribute("disablelist", disabledtoList);
		model.addAttribute("memberGroup", memberGroup); // memberGroup 값을 모델에 추가
		
		return "admin/adminPage";
	}//end adminPage
	
	//회원 목록 요청
	
}//end class
