package net.dima_community.CommunityProject.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.board.BoardReportDTO;
import net.dima_community.CommunityProject.dto.board.JobBoardRecruitDTO;
import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.dto.board.combine.BoardListDTO;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.BoardReportEntity;
import net.dima_community.CommunityProject.entity.board.JobBoardEntity;
import net.dima_community.CommunityProject.entity.board.JobBoardRecruitEntity;
import net.dima_community.CommunityProject.entity.board.LikeEntity;
import net.dima_community.CommunityProject.entity.board.MemberEntity;
import net.dima_community.CommunityProject.repository.board.BoardReportRepository;
import net.dima_community.CommunityProject.repository.board.BoardRepository;
import net.dima_community.CommunityProject.repository.board.JobBoardRecruitRepository;
import net.dima_community.CommunityProject.repository.board.JobBoardRepository;
import net.dima_community.CommunityProject.repository.board.LikeRepository;
import net.dima_community.CommunityProject.repository.board.MemberRepository;
import net.dima_community.CommunityProject.util.FileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardRecruitRepository jobBoardRecruitRepository;
    private final BoardReportRepository boardReportedRepository;
    private final LikeRepository likeRepository;


    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;


    // 페이지 당 글의 개수
    @Value("${user.board.pageLimit}")
    int pageLimit; // 한 페이지 당 게시글 개수

    // ======================== select Entity =======================

    /**
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수 (해당 Entity가 없는 경우 Exception 발생)
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + boardId));
    }

    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity 반환하는 함수 (해당 Entity가 없는 경우 Exception 발생)
     * @param boardId
     * @return
     */
    private JobBoardEntity selectJobBoardEntity (Long boardId){
        return jobBoardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("JobBoard not found with ID: " + boardId));
    }

    /**
     * 전달받은 memberId에 해당하는 MemberEntity 반환하는 함수 (해당 Entity가 없는 경우 Exception 발생)
     * @param memberId
     * @return
     */
    private MemberEntity selectMemberEntity(String memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
    }

    // ======================== 첨부파일 관련 ========================

    /**
     * originalFileName, savedFileName을 멤버변수로 가지고 있는 객체
     */
    private static class FileDetails {
        private final String originalFileName;
        private final String savedFileName;

        public FileDetails(String originalFileName, String savedFileName) {
            this.originalFileName = originalFileName;
            this.savedFileName = savedFileName;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getSavedFileName() {
            return savedFileName;
        }
    }

    /**
     * uploadFile이 null이 아닌 경우 첨부파일을 저장하고, 원본파일명과 저장파일명이 담긴 FileDetails 객체 반환하는 함수
     * @param uploadFile
     * @return FileDetails 객체
     */
    private FileDetails handleFileUpload(MultipartFile uploadFile) {
        if (!uploadFile.isEmpty()) {
            String originalFileName = uploadFile.getOriginalFilename();
            String savedFileName = FileService.saveFile(uploadFile, uploadPath);
            return new FileDetails(originalFileName, savedFileName);
        }
        return null;
    }


    // ========================= 게시글 목록 ========================

    /**
     * 페이지 설정 함수
     * @param page
     * @param pageLimit
     * @return
     */
    private Pageable createPageRequest (int page, int pageLimit){
        return PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "createDate"));
    }

    /**
     * activity/recruit 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchJobBoards(BoardCategory category, String searchWord, Pageable pageable) {
        return jobBoardRepository.findBoardListByCategoryAndTitleContainingAndReportedIsFalse(category, searchWord, pageable);
    }

    /**
     * category가 activity/recruit에 해당하는 게시글 목록 DTO를 리스트로 반환하는 함수
     * @param category
     * @param pageable
     * @param searchWord
     * @return
     */
    public Page<BoardListDTO> selectActivityOrRecruitBoards(BoardCategory category, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // JobBoardEntities 중에 카테고리에 해당하는 게시글들 중 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        // (activity나 recruit인 게시글이 생성될 때, deadline, limitNumber, currentNumber 정보가 없어도 무조건 JobBoardEntity에 값이 추가됨)
        return fetchJobBoards(category,searchWord, pageRequest);
        
    }
    
    /**
     * group 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchGroupBoards(String userGroup, String searchWord, Pageable pageable) {
        return boardRepository.findBoardListByMemberGroupAndTitleContaining(userGroup, searchWord, pageable);
    }
    
    /**
     * category가 group에 해당하는 게시글 목록 DTO를 반환하는 함수
     */
    public Page<BoardListDTO> selectGroupBoards(String userGroup, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // group 카테고리의 BoardEntities 중 
        // memberGroup이 userGroup에 해당하는 게시글들 중 
        // 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        return fetchGroupBoards(userGroup,searchWord, pageRequest);
        
    }


    /**
     * code/project/free/info 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchBoards(BoardCategory category, String searchWord, Pageable pageable) {
        return boardRepository.findBoardListByCategoryAndTitleContaining(category, searchWord, pageable);
    }

    /**
     * category가 activity/recruit/group이 아닌 카테고리에 해당하는 게시글 목록 DTO를 리스트로 반환하는 함수
     * @param category
     * @param pageable
     * @param searchWord
     * @return
     */
    public Page<BoardListDTO> selectOtherCategoryBoards(BoardCategory category, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // 카테고리에 해당하는 BoardEntities 중 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        return fetchBoards(category, searchWord, pageRequest);
    }

    // ======================== 게시글 삭제 ========================

    /**
     * 전달받은 boardId에 해당하는 BoardEntity의 존재여부 확인하는 함수
     */
    private boolean isExist(Long boardId){
        return boardRepository.existsById(boardId);
    }

    /**
     * 전달받은 BoardEntity에 첨부파일이 있는지 확인하는 함수
     */
    private boolean isExistFile(BoardEntity boardEntity){
        return boardEntity.getSavedFileName() != null ? true:false;
    }

    /**
     * 전달받은 BoardEntity 삭제하는 함수
     */
    private void deleteBoardEntity(BoardEntity boardEntity){
        boardRepository.delete(boardEntity);
    }

    /**
     *  전달받은 boardId에 해당하는 게시글 삭제하는 함수
     * @param boardId
     */
    public void deleteOne(Long boardId) {
        // 해당 게시글 존재 여부 확인
        if (isExist(boardId)) {
            // 해당 게시글 가져오기
            BoardEntity entity = selectBoardEntity(boardId); 
            // 첨부파일 있는 경우 삭제
            if (isExistFile(entity)) {
                String fullPath = uploadPath+"/"+entity.getSavedFileName();
                FileService.deleteFile(fullPath);
            } 
            // 해당 게시글 삭제
            deleteBoardEntity(entity);
        }
    }
    

    // ======================== 게시글 생성 ========================

    /**
     * 전달받은 게시글 DTO를 Entity로 변환 후 Board DB에 저장하는 함수
     */
    @Transactional
    public void insertBoard(BoardDTO dto) {
        // 게시글 작성자 데이터(부모테이블) 가져오기
        MemberEntity memberEntity = selectMemberEntity(dto.getMemberId());

        // 첨부파일이 있는 경우 파일 저장 후 DTO의 파일명 세팅
        FileDetails fileDetails = handleFileUpload(dto.getUploadFile());
        if (fileDetails != null) {
            dto.setOriginalFileName(fileDetails.getOriginalFileName());
            dto.setSavedFileName(fileDetails.getSavedFileName());
        }

        // 게시글 DTO -> 엔티티 변환 후 DB 저장
        BoardEntity boardEntity = boardRepository.save(BoardEntity.toEntity(dto, memberEntity));
        
        // activity/recruit 게시글인 경우 
        if (dto.getCategory()==BoardCategory.activity || dto.getCategory()==BoardCategory.recruit) {
            insertJobBoard(boardEntity, dto); // JobBoard DB 저장
        }

    }

    /**
     * 전달받은 BoardDTO로 JobBoardEntity 생성 후 JobBoard DB에 저장하는 함수
     * @param dto
     * @return
     */
    public void insertJobBoard(BoardEntity boardEntity, BoardDTO boardDTO) {
        // JobBoardEntity 생성
        JobBoardEntity jobBoardEntity = JobBoardEntity.builder()
                                                    .deadline(boardDTO.getDeadline())
                                                    .limitNumber(boardDTO.getLimitNumber())
                                                    .currentNumber(0) // DEFAULT : 0
                                                    .build();
        // JobBoardEntity 저장
        JobBoardEntity savedJobBoardEntity = jobBoardRepository.save(jobBoardEntity);
        // BoardEntity 의 jobBoardEntity 값 세팅
        boardEntity.setJobBoardEntity(savedJobBoardEntity); 
    }


    
    // ======================== 게시글 조회 ========================

    

    /**
     * 전달받은 boardId에 해당하는 게시글 DTO반환하는 함수 (job에 관련된 정보가 있는 경우는 해당 정보도 포함해 반환함)
     * @param boardId
     * @return
     */
    public BoardDTO selectOne(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // BoardEntity
        
        // Entity -> DTO로 변환
        BoardDTO boardDTO = BoardDTO.toDTO(boardEntity, boardEntity.getMemberEntity().getMemberId());

        // activity/recruit 게시글인 경우, JobBoardEntity 값을 가져와서 BoardDTO에서 관련 속성값을 세팅
        if (boardDTO.getCategory()==BoardCategory.activity || boardDTO.getCategory()==BoardCategory.recruit) {
            JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // JobBoardEntity
            // deadline, limitNumber, currentNumber 값 세팅
            boardDTO.setDeadline(jobBoardEntity.getDeadline());
            boardDTO.setLimitNumber(jobBoardEntity.getLimitNumber());
            boardDTO.setCurrentNumber(jobBoardEntity.getCurrentNumber());
        }

        return boardDTO;
    }

    /**
     * 전달받은 게시글의 조회수 증가시키는 함수
     * @param boardId
     */
    public void increaseHitCount(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        boardEntity.setHitCount(boardEntity.getHitCount()+1); // 1 증가
    }


    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity의 deadline과 현재시간을 비교해, deadline이 현재 시간보다 이전이면 true, 반대의 경우는 false를 반환하는 함수
     * @param boardId
     * @return
     */
    public boolean isDeadline(Long boardId) {
        JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // 해당 jobBoardEntity
        return jobBoardEntity.getDeadline().isAfter(LocalDateTime.now()) ? false : true; // deadline이 현재 시간보다 이전이면 true 반환
    }
    
    
    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity의 limitNumber와 currentNumber를 비교해 limit수가 current수보다 작거나 같은 경우 true 반환 (큰 경우는 false 반환)하는 함수
     * @param boardId
     * @return
     */
    public boolean isExceededLimitNumber(Long boardId) {
        JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // 해당 jobBoardEntity
        return jobBoardEntity.getLimitNumber()<=jobBoardEntity.getCurrentNumber() ? true : false; // limit 수가 current 수보가 작나 같으면 true 반환
    }

    /**
     * JobBoardRecruit DB에 전달받은 정보에 대한 데이터 존재여부 반환하는 함수
     * @param boardId
     * @param memberId
     * @return 참여 O → true / 참여 X → false
     */
    public boolean isRecruited(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId);       // boardEntity
        MemberEntity memberEntity = selectMemberEntity(memberId);   // memberEntity
        return jobBoardRecruitRepository.findByBoardAndMember(boardEntity, memberEntity).isPresent();
    }
    

    // ======================== 게시글 좋아요 ========================
    
    /**
     * 전달받은 boardId에 해당하는 게시글의 좋아요수 반환하는 함수
     * @param boardId
     * @return
     */
    public long getLikeCount(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        return likeRepository.countByBoardEntity(boardEntity);
    }

    /**
     * 전달받은 memberId에 해당하는 회원이 전달받은 boardId에 해당하는 게시글에 좋아요 눌렀는지 여부 반환하는 함수
     * @param boardId
     * @param memberId
     * @return 좋아요 설정된 상태 → true / 좋아요 해제된 상태 → false
     */
    public boolean isBoardLikedByMember(Long boardId, String memberId) {
        MemberEntity memberEntity = selectMemberEntity(memberId);
        BoardEntity boardEntity = selectBoardEntity(boardId);
        return likeRepository.findByMemberAndBoard(memberEntity, boardEntity).isPresent();
    }

    
    /**
     * member가 board에 대해 이미 좋아요를 눌렀던 상태라면 좋아요 해제하고, 좋아요가 해제된 상태라면 좋아요 설정하는 함수 
     * @param boardId
     * @param memberId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    @Transactional
    public boolean toggleLikeOnBoard(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        MemberEntity memberEntity = selectMemberEntity(memberId); // memberEntity

        Optional<LikeEntity> likeEntityOptional = likeRepository.findByMemberAndBoard(memberEntity, boardEntity);
        
        if (likeEntityOptional.isPresent()) { 
            likeRepository.delete(likeEntityOptional.get()); // delete from Like DB
            boardRepository.decrementLikeCount(boardId); // boardEntity의 likeCount - 1
            return false; // 좋아요 해제
        } else{ 
            // LikeEntity 생성
            LikeEntity likeEntity = LikeEntity.builder()
                                        .boardEntity(boardEntity)
                                        .memberEntity(memberEntity)
                                        .build();
            
            likeRepository.save(likeEntity); // save to Like DB
            boardRepository.incrementLikeCount(boardId); // boardEntity의 likeCount + 1
            return true; // 좋아요 설정
        }
    }

    
    // ======================== 게시글 신고 ========================
    
    /**
     * 게시글 신고 내용이 담긴 DTO를 Entity로 변환 후 DB에 저장하는 함수 
     * @param dto
     */
    public void insertJobBoardReported(BoardReportDTO dto) {
        BoardEntity boardEntity = selectBoardEntity(dto.getBoardId()); // boardEntity
        // 게시글 신고 DTO -> Entity 변환
        BoardReportEntity entity = BoardReportEntity.toEntity(dto, boardEntity);
        // BoardReported DB에 저장
        boardReportedRepository.save(entity);
    }
    
    /**
     * 해당 boardId에 해당하는 게시글의 reported 값을 true로 변환하는 함수
     * @param boardId
     */
    @Transactional
    public void updateRportedCount(Long boardId){
        BoardEntity boardEntity = selectBoardEntity(boardId);        
        //reported 값 true로 변경
        boardEntity.setReported(true);
    }
    
    // ======================== recruit 참여 ========================

    /**
     * recruit 게시글 참여 신청 - JobBoardRecruitDTO를 엔티티로 변환한 후 JobBoardRecruit DB에 저장하는 함수
     * @param jobBoardRecruitDTO
     * @return 참여 성공 → jobBoardRecruitEntity / 참여 실패 → null
     */
    public JobBoardRecruitEntity saveJobBoardRecruit(JobBoardRecruitDTO jobBoardRecruitDTO) { 
        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(jobBoardRecruitDTO.getJobBoardId());
        if (jobBoardEntity.isPresent()) {
            // 부모 Entity 준비
            JobBoardEntity jobBoard = jobBoardEntity.get(); // jobBoardEntity
            MemberEntity member = selectMemberEntity(jobBoardRecruitDTO.getMemberId()); // memberEntity
            // DTO -> Entity
            JobBoardRecruitEntity jobBoardRecruitEntity = JobBoardRecruitEntity.toEntity(jobBoardRecruitDTO, jobBoard, member);
            // JobBoardRecruit DB에 저장
            return jobBoardRecruitRepository.save(jobBoardRecruitEntity);
        } else return null; // 저장 실패
    }


    /**
     * 전달받은 boardId에 해당하는 jobBoardEntity의 currrentNumber 값을 1 증가시키는 함수
     * @param boardId
     */
    @Transactional
    public void updateCurrentNumber(Long boardId) {
        JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId);
        jobBoardEntity.setCurrentNumber(jobBoardEntity.getCurrentNumber()+1);
    }



    // ======================== 게시글 수정 ========================

    @Transactional
    public void updateBoard(BoardDTO boardDTO) {
        // 새롭게 업로드된 파일이 있는 경우 파일 저장 및 이름 추출
        FileDetails newFileDetails = handleFileUpload(boardDTO.getUploadFile());

        // 수정된 내용과 비교를 위해 DB에서 데이터 가져옴
        BoardEntity boardEntity = selectBoardEntity(boardDTO.getBoardId());

        // 기존 파일 처리 및 새로운 파일 저장
        handleExistingFile(boardEntity, newFileDetails);

        // Board 수정 (제목, 내용, 수정날짜)
        updateBoardContent(boardEntity, boardDTO);

        // activity/recruit 게시글인 경우 
        if (boardDTO.getCategory()==BoardCategory.activity || boardDTO.getCategory()==BoardCategory.recruit) {
            // 해당 데이터를 JobBoard DB에서 가져옴
            JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardDTO.getBoardId()); 
            // JobBoard 수정 (마감기한, 모집인원)
            updateJobBoard(jobBoardEntity, boardDTO);
        }
    }

    /**
     * 게시글 수정 시, 
     * 기존 파일 존재 AND 새로운 파일 존재 → 기존 파일 삭제 후 새로운 파일 저장 , 
     * 기존 파일 부재 AND 새로운 파일 존재 → 새로운 파일 저장 
     */
    private void handleExistingFile(BoardEntity boardEntity, FileDetails newFileDetails) {
        String oldSavedFileName = boardEntity.getSavedFileName();

        if (oldSavedFileName != null && newFileDetails != null) {
            // 기존 파일 삭제
            String fullPath = uploadPath + "/" + oldSavedFileName;
            FileService.deleteFile(fullPath);

            // 새로운 파일 정보 업데이트
            boardEntity.setOriginalFileName(newFileDetails.getOriginalFileName());
            boardEntity.setSavedFileName(newFileDetails.getSavedFileName());

        } else if (newFileDetails != null) {
            // 기존 파일은 없고, 새롭게 업로드된 파일이 있는 경우
            boardEntity.setOriginalFileName(newFileDetails.getOriginalFileName());
            boardEntity.setSavedFileName(newFileDetails.getSavedFileName());
        }
    }

    /**
     * BoardEntity의 title, content, updateTime 수정
     */
    private void updateBoardContent(BoardEntity boardEntity, BoardDTO boardDTO) {
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setContent(boardDTO.getContent());
        boardEntity.setUpdateDate(LocalDateTime.now());
    }

    /**
     * JobBoardEntity의 deadline, limitNumber 수정
     * @param boardDTO
     */
    private void updateJobBoard(JobBoardEntity jobBoardEntity, BoardDTO boardDTO){
        jobBoardEntity.setDeadline(boardDTO.getDeadline());
        jobBoardEntity.setLimitNumber(boardDTO.getLimitNumber());
    }

    /**
     * boardId에 해당하는 JobBoardEntity의 deadline을 현재 시간보다 1초전으로 변경하는 함수
     * @param boardId 
     * @return 변경 성공 → true / 변경 실패 → false
     */
    @Transactional
    public boolean updateDeadLine(Long boardId) {
        try {
            JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId);
            jobBoardEntity.setDeadline(LocalDateTime.now().minusSeconds(1)); // 현재 시간 -1초 전으로 수정
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    

}
