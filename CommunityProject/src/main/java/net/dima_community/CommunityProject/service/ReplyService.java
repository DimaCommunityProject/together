package net.dima_community.CommunityProject.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.entity.board.LikeEntity;
import net.dima_community.CommunityProject.entity.board.MemberEntity;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;
import net.dima_community.CommunityProject.repository.board.BoardRepository;
import net.dima_community.CommunityProject.repository.board.LikeRepository;
import net.dima_community.CommunityProject.repository.board.MemberRepository;
import net.dima_community.CommunityProject.repository.board.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    // ====================== select 함수 ======================
    
    /**
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수
     * @param boardId
     * @return boardEntity
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + boardId));
    }

    /**
     * 전달받은 memberId에 해당하는 MemberEntity를 반환하는 함수
     * @param memberId
     * @return memberEntity
     */
    private MemberEntity selectMemberEntity(String memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
    }

    /**
     * 전달받은 replyId에 해당하는 ReplyEntity를 반환하는 함수
     * @param replyId
     * @return replyEntity
     */
    private ReplyEntity selectReplyEntity(Long replyId){
        return replyRepository.findById(replyId).orElseThrow(() -> new EntityNotFoundException("Reply not found with ID: " + replyId));
    }

    // ====================== 게시글 조회 ==========================

    /**
     * 전달받은 boardId에 해당하는 게시글의 총 댓글 수를 반환하는 함수
     * @param boarId
     * @return
     */
    public long getBoardReplyCount(Long boarId) {
        BoardEntity boardEntity = selectBoardEntity(boarId);
        return replyRepository.countByBoardEntity(boardEntity);
    }

    // ====================== 댓글 목록 =====================
    
    // /**
    //  * boardId에 대한 댓글DTO 목록 반환 (likeByUser 세팅하는 로직 포함)
    //  * @param boardId
    //  * @param memberId
    //  * @return
    //  */
    // public List<ReplyDTO> getList(Long boardId, String memberId) {
    //     BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
    //     List<ReplyEntity> replyEntities = replyRepository.findByBoardEntity(boardEntity); // boardEntity의 댓글 목록 가져옴

    //     return replyEntities.stream().map(reply ->{
    //         boolean isLikeByUser = likeRepository.existsByReplyIdAndMemberId(reply.getReplyId(), memberId); // user의 좋아요 여부 
    //         return ReplyDTO.builder()
    //             .replyId(reply.getReplyId())
    //             .boardId(reply.getBoardEntity().getBoardId())
    //             .parentReplyId((reply.getParentReplyId()))
    //             .memberId(reply.getMemberEntity().getMemberId())
    //             .content(reply.getContent())
    //             .createDate(reply.getCreateDate())
    //             .updateDate(reply.getUpdateDate())
    //             .likeCount(reply.getLikeCount())
    //             .likeByUser(isLikeByUser)
    //             .build();
    //     }).collect(Collectors.toList());
    // }

    /**
     * boardId에 대한 댓글DTO 목록 반환 (likeByUser, childReplies 세팅하는 로직 포함)
     * @param boardId
     * @param memberId
     * @return
     */
    public List<ReplyDTO> getList(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId);   // BoardEntity
        List<ReplyEntity> replyEntities = replyRepository.findByBoardEntity(boardEntity); // boardEntity의 댓글 목록 가져옴

        // 댓글 ID를 키로 하고, 대댓글들을 리스트로 갖는 맵 생성
        Map<Long, List<ReplyDTO>> replyTree = new HashMap<>();

        // 모든 ReplyDTO를 트리 구조로 생성
        List<ReplyDTO> rootReplies = new ArrayList<>();

        for (ReplyEntity replyEntity : replyEntities) {
            // 로그인한 사용자의 죻아요 여부 확인
            boolean isLikeByUser = likeRepository.existsByReplyIdAndMemberId(replyEntity.getReplyId(), memberId);
            ReplyDTO replyDTO = ReplyDTO.builder()
                                        .replyId(replyEntity.getReplyId())
                                        .boardId(replyEntity.getBoardEntity().getBoardId())
                                        .parentReplyId((replyEntity.getParentReplyId()))
                                        .memberId(replyEntity.getMemberEntity().getMemberId())
                                        .content(replyEntity.getContent())
                                        .createDate(replyEntity.getCreateDate())
                                        .updateDate(replyEntity.getUpdateDate())
                                        .likeCount(replyEntity.getLikeCount())
                                        .likeByUser(isLikeByUser)
                                        .build();
            // 부모 댓글이 없는 경우 rootReplies에 추가
            if (replyEntity.getParentReplyId()==null) {
                rootReplies.add(replyDTO);
            }else{
                // 부모 댓글이 있는 경우 해당 부모의 대댓글 리스트에 추가
                replyTree.computeIfAbsent(replyEntity.getParentReplyId(), k-> new ArrayList<>()).add(replyDTO);
            }
        }

        // 이제 부모 댓글에 각 대댓글을 트리구조로 추가
        for(ReplyDTO replyDTO: rootReplies){
            addChildReplies(replyDTO, replyTree);
        }

        return rootReplies; // 최상위 댓글 (대댓글을 포함된 트리구조 반환)
    }

    /**
     * parentDTO의 childReplies 즉, 대댓글을 추가하는 함수
     * @param parentDTO
     * @param replyTree
     */
    private void addChildReplies(ReplyDTO parentDTO, Map<Long, List<ReplyDTO>>replyTree){
        List<ReplyDTO> childReplies = replyTree.get(parentDTO.getReplyId());
        if (childReplies!=null) {
            parentDTO.setChildReplies(childReplies); // 대댓글 목록 세팅
            // for(ReplyDTO child : childReplies){
            //     addChildReplies(child, replyTree); // 재귀 호출 (대댓글의 댓글까지 구현하고자 하려면 사용!)
            // }
        }
    }

    // ====================== 댓글 등록 =====================

    /**
     * 해당 댓글 DTO를 Entity로 변환 후 DB에 저장하는 함수
     * @param replyDTO
     */
    @Transactional
    public void createOne(ReplyDTO replyDTO) {
        BoardEntity boardEntity = selectBoardEntity(replyDTO.getBoardId());  // boardEntity
        MemberEntity memberEntity = selectMemberEntity(replyDTO.getMemberId()); // memberEntity
        ReplyEntity replyEntity = ReplyEntity.toEntity(replyDTO, boardEntity, memberEntity); // DTO -> Entity 변환
        replyRepository.save(replyEntity); // save to Reply
        replyCountPlus(boardEntity); // Board의 replyCount 증가
    }

    /**
     * boardEntity의 replyCount 1 증가시키는 함수
     * @param boardEntity
    */
    public void replyCountPlus(BoardEntity boardEntity) {
        boardEntity.setReplyCount(boardEntity.getReplyCount()+1);
    }


    // ====================== 댓글 수정 =====================

    /**
     * 해당 Entity의 일부 속성(content, updateDate)을 전달된 값으로 수정하는 함수
     * @param replyDTO
     */
    @Transactional
    public void updateOne(ReplyDTO replyDTO) {
        ReplyEntity replyEntity = selectReplyEntity(replyDTO.getReplyId()); // 기존 ReplyEntity
        updateReplyContent(replyEntity, replyDTO); // Reply 수정 (content, updateDate)
    }

    /**
     * Reply의 content, updateDate 수정 함수
     * @param replyEntity
     * @param replyDTO
     */
    private void updateReplyContent(ReplyEntity replyEntity, ReplyDTO replyDTO){
        replyEntity.setContent(replyDTO.getContent());
        replyEntity.setUpdateDate(replyDTO.getUpdateDate());
    }

    
    // ====================== 댓글 삭제 =====================
    
    /**
     * 전달 받은 replyId에 해당하는 댓글 데이터 삭제하는 함수
     * @param replyId
     */
    @Transactional
    public void deleteOne(Long replyId) {
        // 해당 댓글 엔티티
        ReplyEntity replyEntity = selectReplyEntity(replyId); 
        // 댓글이 등록된 BoardEntity
        BoardEntity boardEntity = replyEntity.getBoardEntity(); 
        // 전달받은 replyId에 해당하는 댓글 삭제
        replyRepository.deleteById(replyId); 
        // 자식 reply의 개수  
        int totalReplyCount = replyRepository.countByReplyIdInParent(replyId);
        // 자식 reply들 삭제
        if(totalReplyCount>0){
            replyRepository.deleteByParentReplyId(replyId);
        }
        // BoardEntity의 replyCount의 값 (totalReplyCount+1)만큼 감소
        replyCountMinus(boardEntity, 1+totalReplyCount); 
    }

    /**
     * boardEntity의 replyCount를 totalReplyCount만큼 감소시키는 함수
     * @param boardEntity
     * @param totalReplyCount : 삭제된 댓글 및 대댓글 총 개수
    */
    public void replyCountMinus(BoardEntity boardEntity, int totalReplyCount) {
        int replyCount = boardEntity.getReplyCount();
        if (replyCount - totalReplyCount <= 0) {
            boardEntity.setReplyCount(0);
        } else {
            boardEntity.setReplyCount(replyCount - totalReplyCount);
        }        
    }
    
    
    // ====================== 댓글 좋아요 =====================

    /**
     * member가 reply에 대해 이미 좋아요를 눌렀던 상태라면 좋아요 해제하고, 좋아요가 해제된 상태라면 좋아요 설정하는 함수
     * @param replyId
     * @param memberId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    @Transactional
    public boolean toggleLikeOnReply(Long replyId, String memberId) {
        ReplyEntity replyEntity = selectReplyEntity(replyId); // ReplyEntity
        MemberEntity memberEntity = selectMemberEntity(memberId); // MemberEntity

        Optional<LikeEntity> likeEntityOptional = likeRepository.findByMemberAndReply(memberEntity, replyEntity);

        if(likeEntityOptional.isPresent()){
            likeRepository.delete(likeEntityOptional.get()); // delete from Like DB
            replyEntity.setLikeCount(replyEntity.getLikeCount()-1); // likeCount - 1 
            return false; // 좋아요 해제
        }else{
            // 좋아요 데이터 생성 
            LikeEntity likeEntity = LikeEntity.builder()
                                                .replyEntity(replyEntity)
                                                .memberEntity(memberEntity)
                                                .build();
            likeRepository.save(likeEntity); // save to Like DB
            replyEntity.setLikeCount(replyEntity.getLikeCount()+1); // likeCount + 1
            return true; // 좋아요 해제
        }
    }

    /**
     * replyId에 해당하는 reply의 likeCount 반환
     * @param replyId
     * @return
     */
    public int getLikeCount(Long replyId) {
        ReplyEntity replyEntity = selectReplyEntity(replyId);
        return replyEntity.getLikeCount();
    }

    // ====================== 대댓글 =====================

    /**
     * 부모 댓글 존재하는지 확인하는 함수
     * @param parentReplyId
     * @return 존재 → true 
     */
    public boolean existsParentReply(Long parentReplyId) {
        return replyRepository.existsById(parentReplyId);
    }

    
}
