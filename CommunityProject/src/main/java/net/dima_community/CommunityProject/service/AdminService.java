package net.dima_community.CommunityProject.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.util.FileService;
import net.dima_community.CommunityProject.dto.AdminNoteDTO;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.AdminNoteEntity;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.AdminNoteRepository;
import net.dima_community.CommunityProject.repository.MemberRepository;


@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
	private final MemberRepository memberRepository;
	private final AdminNoteRepository adminNoteRepository;

	@Value("${admin.page.pageLimit}")
	int pageLimit;

	// 업로드된 파일이 저장될 폴더 경로를 읽어옴
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;

	// 승인된 회원 목록 가져오기
	public Page<MemberDTO> selectEnableAll(Pageable pageable, String memberGroup) {
		int page = pageable.getPageNumber() - 1; // 사용자가 요청한 페이지 번호. default 1

		Page<MemberEntity> entityList = null;

		entityList = memberRepository.findByMemberGroup(memberGroup,
				PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "memberName")));

		log.info("관리자 페이지 회원 테이블(서비스단) : {}", entityList.getContent());

		Page<MemberDTO> dtoList = null;

		// 보여줄 내용만 추려서 생성
		dtoList = entityList.map(member -> new MemberDTO(member.getMemberName(), member.getMemberEmail(),
				member.getMemberGroup(), member.getMemberId(), member.getMemberEnabled()));
		return dtoList;
	}// end selectAll

	// 승인 안된 회원 목록 가져오기
	public List<MemberDTO> selectDisableAll() {

		List<MemberEntity> entityList = null;
		entityList = memberRepository.findByMemberEnabled("n");

		// log.info("관리자 페이지 회원 테이블(서비스단) : {}", entityList.getContent());

		List<MemberDTO> dtoList = null;

		// 보여줄 내용만 추려서 생성
		return entityList
				.stream().map(member -> new MemberDTO(member.getMemberName(), member.getMemberEmail(),
						member.getMemberGroup(), member.getMemberId(), member.getMemberEnabled()))
				.collect(Collectors.toList());
	}//end selectDisableAll
	
	//공지사항 목록 가져오기
	public Page<AdminNoteDTO> selectNoteAll(Pageable pageable) {
		int page = pageable.getPageNumber() - 1;
		
		Pageable pageable1 = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "adminNoteNum"));
		 
		Page<AdminNoteEntity> entityList = adminNoteRepository.findAll(pageable1);
		
		log.info("관리자 페이지 공지사항 테이블(서비스단) : {}", entityList.getContent());

		Page<AdminNoteDTO> dtoList = null;
		
		// 앞단으로 가져갈 내용만 추려서 생성
		dtoList = entityList.map(adminNote ->
			new AdminNoteDTO(adminNote.getAdminNoteNum(),
					adminNote.getAdminNoteTitle(),
					adminNote.getAdminNoteHitcount(),
					adminNote.getAdminNoteCreateDate(),
					adminNote.getAdminNoteOriginalFileName()));
		return dtoList;
	}// end selectNoteAll
	
	//공지사항 db에 글 저장
	public boolean insertAdminNote(AdminNoteDTO adminNoteDTO) {
		log.info("저장경로 : {}", uploadPath);
		
		String adminNoteOriginalFileName = null;
		String adminNoteSavedFileName = null;
		
		//첨부파일이 있으면 파일명 처리 실시
		if(!adminNoteDTO.getUploadFile().isEmpty()) {
			adminNoteSavedFileName = FileService.saveFile(adminNoteDTO.getUploadFile(), uploadPath);
			adminNoteOriginalFileName = adminNoteDTO.getUploadFile().getOriginalFilename();
			
			adminNoteDTO.setAdminNoteOriginalFileName(adminNoteOriginalFileName);
			adminNoteDTO.setAdminNoteSavedFileName(adminNoteSavedFileName);
		}
		AdminNoteEntity adminNoteEntity = AdminNoteEntity.toEntity(adminNoteDTO);
		
		try {
			AdminNoteEntity savedEntity = adminNoteRepository.save(adminNoteEntity);
			return savedEntity != null;
		} catch (Exception e) {
			log.error("공지사항 저장 실패", e);
			return false;
		}
	}//end insertAdminNote
	
	//공지사항 상세 페이지
	public AdminNoteDTO selectNoteOne(Long adminNoteNum) {
		Optional<AdminNoteEntity> entity = adminNoteRepository.findById(adminNoteNum);
		
		if(entity.isPresent()) {
			AdminNoteEntity adminNoteEntity  = entity.get();
			AdminNoteDTO dto = AdminNoteDTO.toDTO(adminNoteEntity);
			return dto;
		}
		return null;
	}//end selectNoteOne
	
	//조회수 증가
	@Transactional
	public void incrementHitcount(Long adminNoteNum) {
		Optional<AdminNoteEntity> entity = adminNoteRepository.findById(adminNoteNum);
		log.info("조회수 증가 서비스 단 1: {}", entity.toString());
		
		if(entity.isPresent()) {
			AdminNoteEntity adminNoteEntity  = entity.get();
			adminNoteEntity.setAdminNoteHitcount(adminNoteEntity.getAdminNoteHitcount() + 1);
			
			log.info("조회수 증가 서비스 단 2: {}", adminNoteEntity.toString());
		}
		
		
	}//end incrementHitcount
	
	//공지사항 수정
	@Transactional
	public void updateNoteOne(AdminNoteDTO adminNoteDTO) {
		log.info("공지사항 수정 서비스단 1: {}", adminNoteDTO.toString());
		
		MultipartFile uploadFile = adminNoteDTO.getUploadFile(); //첨부파일
		
		String originalFileName = null;	//새로운 첨부파일이 있을 때(사용자가 쓴 원래 이름)
		String savedFileName = null;	//새로운 첨부파일이 있을 때(랜덤값 넣어서 조작한 이름)
		String oldSavedFileName = null;	//기존 업로드된 파일이 있을 때(DB)
		
		// 수정 시 업로드한 파일이 있는지 확인
		if(!uploadFile.isEmpty()) {
			originalFileName = uploadFile.getOriginalFilename();
			savedFileName = FileService.saveFile(uploadFile, uploadPath);
		}
		
		Optional<AdminNoteEntity> entity = adminNoteRepository.findById(adminNoteDTO.getAdminNoteNum());
		
		log.info("공지사항 수정 서비스단 2: {}", entity.toString());
		
		if(entity.isPresent()) {
			AdminNoteEntity adminNoteEntity = entity.get();
			oldSavedFileName = adminNoteEntity.getAdminNoteOriginalFileName();
			
			// 1. 기존파일이 있고, 업로드한 파일도 있을 경우 기존 파일 삭제
			if(oldSavedFileName != null && !uploadFile.isEmpty()) {
				String fullPath = uploadPath + "/" + oldSavedFileName;
				FileService.deleteFile(fullPath);
				
				adminNoteEntity.setAdminNoteOriginalFileName(originalFileName);
				adminNoteEntity.setAdminNoteSavedFileName(savedFileName);
			}
			// 2. 기존파일은 없고, 업로드된 파일이 있을 경우 바로 db 저장
			else if(oldSavedFileName == null && !uploadFile.isEmpty()) {
				adminNoteEntity.setAdminNoteOriginalFileName(originalFileName);
				adminNoteEntity.setAdminNoteSavedFileName(savedFileName);
			}
			// 3. 첨부파일이 아예 없는 경우 파일 빼고 다 set함
			log.info("공지사항 수정 서비스단 3: {}", adminNoteDTO.toString());
			
			adminNoteEntity.setAdminNoteTitle(adminNoteDTO.getAdminNoteTitle());
			adminNoteEntity.setAdminNoteContent(adminNoteDTO.getAdminNoteContent());
			adminNoteEntity.setAdminNoteUpdateDate(LocalDateTime.now());
			
			log.info("공지사항 수정 서비스단 4: {}", adminNoteEntity.toString());
		}
	}//end updateNoteOne
	
	//공지사항 삭제
	@Transactional
	public void delectNoteOne(Long adminNoteNum) {
		Optional<AdminNoteEntity> entity = adminNoteRepository.findById(adminNoteNum);
		
		if(entity.isPresent()) {
			AdminNoteEntity noteEntity = entity.get();
			
			String savedFileName = noteEntity.getAdminNoteSavedFileName();
			
			//첨부 파일이 있으면 파일 삭제 후 글 삭제
			if(savedFileName != null) {
				String fullPath = uploadPath + "/" + savedFileName;
				FileService.deleteFile(fullPath);
			}
			adminNoteRepository.deleteById(adminNoteNum);
		}
	}//end delectNoteOne
}//end class
