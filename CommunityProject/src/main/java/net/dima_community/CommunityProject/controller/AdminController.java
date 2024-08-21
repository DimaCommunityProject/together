package net.dima_community.CommunityProject.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.util.PageNavigator;
import net.dima_community.CommunityProject.dto.AdminNoteDTO;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.service.AdminService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminservice;

	@Value("${admin.page.pageLimit}")
	int pageLimit; // 한페이지에 보여줄 글의 개수

	// 파일의 저장경로
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;

	// 관리자 페이지 요청
	@GetMapping("/admin/adminPage")
	public String adminPage(@PageableDefault(page = 1) Pageable pageable // Pageable : 페이징을 해주는 객체.
			, Model model, @RequestParam(name = "memberGroup", defaultValue = "1기") String memberGroup) {
//		log.info("페이지이이이이이이 : {}", pageable);
//		log.info("페이지이이이이이이 : {}", memberGroup);
		Page<MemberDTO> enabledtoList = adminservice.selectEnableAll(pageable, memberGroup);

		// 페이지 번호 자동계산 해주는 PageNavigator 객체 생성
		int totalPages = (int) enabledtoList.getTotalPages();
		int page = pageable.getPageNumber();
		PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);

		// 승인 안된 회원 불러오기
		List<MemberDTO> disabledtoList = adminservice.selectDisableAll();

		model.addAttribute("enablelist", enabledtoList);
		model.addAttribute("navi", navi);
		model.addAttribute("disablelist", disabledtoList);
		model.addAttribute("memberGroup", memberGroup); // memberGroup 값을 모델에 추가

		return "admin/adminPage";
	}// end adminPage

	// 공지사항 페이지 요청
	// 요청과 동시에 공지사항 목록 보여줌
	@GetMapping("/admin/adminPageNote")
	public String adminPageNotice(@PageableDefault(page = 1) Pageable pageable, Model model) {
		Page<AdminNoteDTO> dtoList = adminservice.selectNoteAll(pageable);

		// 페이지 번호 자동계산 해주는 PageNavigator 객체 생성
		int totalPages = (int) dtoList.getTotalPages();
		int page = pageable.getPageNumber();
		PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);

		model.addAttribute("list", dtoList);
		model.addAttribute("navi", navi);

		return "admin/adminPageNote";
	}// end adminPageNotice

	// 공지사항 글쓰기 요청
	@GetMapping("/admin/adminPageNoteWrite")
	public String adminPageWrite() {
		return "admin/adminPageNoteWrite";
	}// end adminPageWrite

	// 공지사항 글 db 등록
	@PostMapping("/admin/adminPageNoteWrite")
	public String adminPageWrite(@ModelAttribute AdminNoteDTO adminNoteDTO, Model model) {
		log.info("글 DB 저장 요청 : {}", adminNoteDTO.toString());
		log.info("첨부파일명 : {}", adminNoteDTO.getUploadFile().getOriginalFilename());
		log.info("첨부파일크기 : {}", adminNoteDTO.getUploadFile().getSize());

		Boolean savedDB = adminservice.insertAdminNote(adminNoteDTO); // db에 저장하고 옴

//		if(savedDB) {model.addAttribute("success", "공지사항 등록이 완료되었습니다.");}
//		else {model.addAttribute("fail", "공지사항 등록을 실패했습니다.");}

		return "redirect:/admin/adminPageNote";
	}// end adminPageWrite
	
	// 공지사항 상세 조회
	@GetMapping("/admin/adminPageNoteDetail")
	public String adminPageDetail(@RequestParam(name = "adminNoteNum") Long adminNoteNum, Model model, HttpSession session, @PageableDefault(page = 1) Pageable pageable) {
		AdminNoteDTO adminNoteDTO = adminservice.selectNoteOne(adminNoteNum);
		adminservice.incrementHitcount(adminNoteNum); // 조회수 증가
		model.addAttribute("adminNote", adminNoteDTO);

		return "admin/adminPageNoteDetail";
	}// end adminPageDetail

	// 공지사항 수정
	@GetMapping("/admin/adminPageNoteUpdate")
	public String adminPageNoteUpdate(@RequestParam(name = "adminNoteNum") Long adminNoteNum, Model model) {
		AdminNoteDTO adminNoteDTO = adminservice.selectNoteOne(adminNoteNum);
		model.addAttribute("adminNote", adminNoteDTO);
		return "admin/adminPageNoteUpdate";
	}// end adminPageNoteUpdate

	// 공지사항 수정
	@PostMapping("/admin/adminPageNoteUpdate")
	public String adminPageNoteUpdate(@RequestParam(name = "adminNoteNum") Long adminNoteNum, Model model,
			RedirectAttributes rttr, @ModelAttribute AdminNoteDTO adminNoteDTO) {
		
		log.info("남바 : {}", adminNoteNum);
		log.info("==========={}", adminNoteDTO.toString());

		adminservice.updateNoteOne(adminNoteDTO);
		
		log.info("공지사항 수정하고 dto 어떻게 됐나요? : {}", adminNoteDTO.toString());
		
		rttr.addAttribute("adminNoteNum", adminNoteDTO.getAdminNoteNum());

		return "redirect:/admin/adminPageNoteDetail";
	}// end adminPageNoteUpdate

	// detail쪽에서 첨부파일 다운로드 받을 때
	@GetMapping("/admin/adminFileDownload")
	public String adminFileDownload(@RequestParam(name = "adminNoteNum") Long adminNoteNum,
			HttpServletResponse response) {

		AdminNoteDTO adminNoteDTO = adminservice.selectNoteOne(adminNoteNum);
		String adminNoteOriginalFileName = adminNoteDTO.getAdminNoteOriginalFileName();
		String adminNoteSavedFileName = adminNoteDTO.getAdminNoteSavedFileName();

		log.info("오리지날 : {}", adminNoteOriginalFileName);
		log.info("저장명 : {}", adminNoteSavedFileName);

		// 첫번째 try~catch : 깨지지않게 (한글) 설정
		try {
			String tempName = URLEncoder.encode(adminNoteOriginalFileName, StandardCharsets.UTF_8.toString());
			response.setHeader("Content-Disposition", "attachment;filename=" + tempName); // 안해주면 브라우저에서 실행됨

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String fullPath = uploadPath + "/" + adminNoteSavedFileName;
		
		// 스트림설정(실제 다운로드)
		FileInputStream filein = null;
		ServletOutputStream fileout = null;
		
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			
			FileCopyUtils.copy(filein, fileout);	//(filein 원본 복사해서 fileout으로 붙임)
			
			// close할 때는 연거의 반대 순서로 닫기
			fileout.close(); 
			filein.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}// end adminFileDownload
	
	//공지사항 삭제
	@GetMapping("/admin/adminPageNoteDelete")
	public String adminPageNoteDelete(@RequestParam(name = "adminNoteNum") Long adminNoteNum, Model model
			, RedirectAttributes rttr) {
		
		adminservice.delectNoteOne(adminNoteNum);
		
		return "redirect:/admin/adminPageNote";
	}
}// end class
