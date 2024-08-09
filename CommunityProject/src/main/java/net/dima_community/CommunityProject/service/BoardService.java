package net.dima_community.CommunityProject.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.board.BoardReportDTO;
import net.dima_community.CommunityProject.dto.board.JobBoardDTO;
import net.dima_community.CommunityProject.dto.board.check.BoardCategory;
import net.dima_community.CommunityProject.dto.board.combine.BoardListDTO;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.BoardReportEntity;
import net.dima_community.CommunityProject.entity.board.JobBoardEntity;
import net.dima_community.CommunityProject.entity.board.MemberEntity;
import net.dima_community.CommunityProject.repository.board.BoardReportRepository;
import net.dima_community.CommunityProject.repository.board.BoardRepository;
import net.dima_community.CommunityProject.repository.board.JobBoardRepository;
import net.dima_community.CommunityProject.repository.board.MemberRepository;
import net.dima_community.CommunityProject.util.FileService;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final JobBoardRepository jobBoardRepository;
    private final MemberRepository memberRepository;
    private final BoardReportRepository boardReportedRepository;


    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;


    // 페이지 당 글의 개수
    @Value("${user.board.pageLimit}")
    int pageLimit; // 한 페이지 당 게시글 개수


    // ======================== 기본 CRUD ========================

    /**
     * 전달받은 boardId에 해당하는 게시글 엔티티 반환하는 함수
     * @param boardId
     * @return
     */
    public BoardDTO selectOne(Long boardId) {
        BoardDTO dto = new BoardDTO();
        
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardId);
        // 게시글 Entity->DTO변환
        if (boardEntity.isPresent()) {
            BoardEntity entity = boardEntity.get();
            dto = BoardDTO.toDTO(entity, entity.getMemberEntity().getMemberId());
        }
        return dto;
    }


    /**
     * 수정된 게시글 DTO를 받아서 수정해서 DB에 저장하는 함수
     * @param jobBoardDTO
     */
    @Transactional
    public void updateOne(BoardDTO boardDTO){
        // 수정된 게시글의 첨부파일 
        MultipartFile uploadFile = boardDTO.getUploadFile();

        String originalFileName = null;
        String savedFileName = null;
        String oldSavedFileName = null;

        // 수정 작업에서 새롭게 업로드된 파일이 있는 경우
        // 파일을 저장장치에 저장하고, 이름 추출
        if (!uploadFile.isEmpty()) {
            originalFileName = uploadFile.getOriginalFilename();
            savedFileName = FileService.saveFile(uploadFile, uploadPath);
        }

        // 수정된 내용과 비교를 위해 DB에서 데이터 가져옴
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardDTO.getBoardId());
        if (boardEntity.isPresent()) {
            BoardEntity entity = boardEntity.get();
            oldSavedFileName = entity.getSavedFileName();

            // 기존 파일이 있고, 업로드 파일도 있다면, 원래 저장된 파일은 삭제 & 새로운 파일은 저장
            if (oldSavedFileName!=null && !uploadFile.isEmpty()) {
                // 기존 파일 저장장치에서 삭제
                String fullPath = uploadPath+"/"+oldSavedFileName;
                FileService.deleteFile(fullPath);
                // 새로운 파일 저장
                entity.setOriginalFileName(originalFileName);
                entity.setSavedFileName(savedFileName);
                
            }
            // 기존 파일은 없고, 새롭게 업로드한 파일이 있는 경우
            else if(oldSavedFileName==null && !uploadFile.isEmpty()){
                // 새로운 파일 저장
                entity.setOriginalFileName(originalFileName);
                entity.setSavedFileName(savedFileName);
            }

            // 기존 파일도 없고, 새로운 파일도 없으면 파일 처리 과정 생략
            // 나머지 정보 update (제목, 내용) 
            entity.setTitle(boardDTO.getTitle());
            entity.setContent(boardDTO.getContent());
            // entity.setDeadline(boardDTO.getDeadline());
            // entity.setLimitNumber(boardDTO.getLimitNumber());
        }

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
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).get();
    }

    /**
     * 전달받은 BoardEntity에 첨부파일이 있는지 확인하는 함수
     */
    private boolean isExistFile(BoardEntity boardEntity){
        return boardEntity.getSavedFileName() != null ? true:false;
    }

    /**
     * 전달받은 BoardEntity의 첨부파일 삭제하는 함수
     */
    private void deleteFile(BoardEntity boardEntity){
        String fullPath = uploadPath+"/"+boardEntity.getSavedFileName();
        FileService.deleteFile(fullPath);
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
            if (isExistFile(entity))  deleteFile(entity);
            // 해당 게시글 삭제
            deleteBoardEntity(entity);
        }
    }
    

    // ======================== 게시글 생성 ========================
    
    /**
     * 전달받은 memberId에 해당하는 게시글 작성자 MemberEntity 반환하는 함수
     * @param memberId
     * @return
     */
    private MemberEntity findMemberEntity(String memberId){
        return memberRepository.findById(memberId).get();
    }

    /**
     * 전달받은 게시글 DTO의 첨부파일 여부 확인하는 함수 
     */
    private boolean uploadFileisExist(BoardDTO dto){
        return !dto.getUploadFile().isEmpty();
    }
    
    /**
     * 전달받은 게시글 DTO에 첨부파일이 존재하여 첨부파일 저장 및 파일명을 세팅하는 함수
     */
    private void saveFile(BoardDTO dto){
        String originalFileName = dto.getUploadFile().getOriginalFilename();
        String savedFileName = FileService.saveFile(dto.getUploadFile(), uploadPath);

        dto.setOriginalFileName(originalFileName);
        dto.setSavedFileName(savedFileName);
    }

    /**
     * 전달받은 게시글 DTO를 Entity로 변환 후 Board DB에 저장하는 함수
     */
    public void insertBoard(BoardDTO dto) {

        MemberEntity memberEntity = findMemberEntity(dto.getMemberId()); // 작성자 Entity

        // 첨부파일이 있으면 파일저장 및 파일명 세팅
        if (uploadFileisExist(dto)) {
            saveFile(dto);
        }

        // 게시글 DTO -> 엔티티 변환 후 DB 저장
        boardRepository.save(BoardEntity.toEntity(dto, memberEntity));
    }

    /**
     * 전달받은 JobBoardDTO를 Entity로 변환 후 JobBoard DB에 저장하고, 저장여부 반환하는 함수
     * @param dto
     * @return
     */
    public void insertJobBoard(JobBoardDTO dto) {
        BoardEntity boardEntity = selectBoardEntity(dto.getBoardId()); // 게시글 Entity 
        jobBoardRepository.save(JobBoardEntity.toEntity(dto, boardEntity)); // JobBoard에 저장 
    }
    

    
    // ======================== 게시글 신고 ========================

    /**
     * 해당 boardId에 해당하는 게시글의 reported 값을 true로 변환하는 함수
     * @param boardId
     */
    @Transactional
    public void updateRportedCount(Long boardId){
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardId);
        
        if (boardEntity.isPresent()) {
            BoardEntity entity = boardEntity.get();
            //reported 값 true로 변경
            entity.setReported(true);
        }
    }

    /**
     * 게시글 신고 내용이 담긴 DTO를 Entity로 변환 후 DB에 저장하는 함수 
     * @param dto
     */
    public void insertJobBoardReported(BoardReportDTO dto) {

        // 신고당한 게시글 엔티티
        BoardEntity boardEntity = boardRepository.findById(dto.getBoardId()).get();

        // 게시글 신고 DTO -> Entity 변환 후 DB에 저장
        BoardReportEntity entity = BoardReportEntity.toEntity(dto, boardEntity);
        boardReportedRepository.save(entity);

        // 해당 게시글의 reported 컬럼 true로 변경
        updateRportedCount(dto.getBoardId());
    }















    
    
}
