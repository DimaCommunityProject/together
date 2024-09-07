package net.dima_community.CommunityProject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.AdminNoteDTO;
import net.dima_community.CommunityProject.entity.AdminNoteEntity;
import net.dima_community.CommunityProject.repository.AdminNoteRepository;

@RequiredArgsConstructor
@Slf4j
@Service
public class MainService {
	private final AdminNoteRepository adminNoteRepository;


	// ===================== 공지사항 불러오기(최신순 3개) =====================

	/**
	 * 공지사항 목록 가져오기
	 * 
	 * @return
	 */
	public List<AdminNoteDTO> selectNoteAll() {

		List<AdminNoteEntity> entityList = null;
		//entityList = adminNoteRepository.findAll(Sort.by(Sort.Direction.DESC, "adminNoteCreateDate"));

		entityList = adminNoteRepository.findTop5ByOrderByAdminNoteCreateDateDesc();
										 
		log.info("메인페이지 공지사항(서비스단) : {}", entityList.toString());

		// 앞단으로 가져갈 내용만 추려서 생성
		return entityList.stream().map(adminNote -> new AdminNoteDTO(adminNote.getAdminNoteNum(),
				adminNote.getAdminNoteTitle(),
				adminNote.getAdminNoteHitcount(),
				adminNote.getAdminNoteCreateDate(),
				adminNote.getAdminNoteOriginalFileName()))
				.collect(Collectors.toList());
	}// end selectNoteAll

}