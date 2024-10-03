package net.dima_community.CommunityProject.controller.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.entity.member.MemberEntity;
import net.dima_community.CommunityProject.service.board.BoardService;
import net.dima_community.CommunityProject.service.board.ReplyService;
import net.dima_community.CommunityProject.service.member.MemberService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;
    private final BoardService boardService;
    private final MemberService memberService;

    // ============== 마이페이지 ================
    /**
     * ajax - 마이페이지에서 사용자가 작성한 댓글 목록 요청
     * 
     * @param memberId
     * @return
     */
    @PostMapping("/reply/showReply")
    @ResponseBody
    public Map<String, BoardDTO> showReply(@RequestParam(name = "memberId") String memberId) {
        MemberDTO member = memberService.findById(memberId);
        List<ReplyDTO> replyList = replyService.findByMemberId(MemberEntity.toEntity(member));
        Map<String, BoardDTO> dataMap = new HashMap<>();
        for (ReplyDTO temp : replyList) {
            BoardDTO board = boardService.findById(temp.getBoardId());
            dataMap.put(temp.getContent(), board);
        }
        return dataMap;
    }

    // ============ 게시글 댓글 수 =====================
    /**
     * ajax - 게시글의 댓글 수 요청
     * 
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/getReplyCount")
    public long getReplyCount(@RequestParam(name = "boardId") Long boarId) {
        return replyService.getBoardReplyCount(boarId);
    }

    // ================== 댓글 목록 ==================
    // list로 보내지 않고 RequestMapping으로 보내야겠다 ㅎㅎ
    // 원래 html을 보면서 같이 해야 하는데.. 지금 프로젝트 실행이 안되므로 .... 허허
    // 일단 감으로 해보겠습니다.

    /**
     * ajax - 게시글에 대한 댓글 목록 요청
     * 
     * @param boardId
     * @param memberId (로그인한 사용자)
     * @param model
     * @return
     */
    @RequestMapping(value = "/reply/getList", method = RequestMethod.GET)
    public String getList(@RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "memberId") String memberId, Model model) {

        List<ReplyDTO> replyDTOs = new ArrayList<>();

        replyDTOs = replyService.getList(boardId, memberId);

        model.addAttribute("list", replyDTOs);

        return "board/detail::#result";
    }

    // ================== 댓글 등록 ==================

    /**
     * ajax - 댓글 등록 처리 요청
     * 
     * @param boardId
     * @param memberId (로그인한 사용자)
     * @param content
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/create")
    public String create(@RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "content") String content) {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .boardId(boardId)
                .parentReplyId(null)
                .memberId(memberId)
                .content(content)
                .createDate(LocalDateTime.now())
                .updateDate(null)
                .likeCount(0)
                .build();
        replyService.createOne(replyDTO);

        return ""; // 뭘 반환해여햐지? -> 댓글 목록 요청 함수를 호출하면 되지 않을까?
    }

    // ================== 댓글 수정 ==================

    /**
     * ajax - 댓글 내용 수정 처리 요청
     * 
     * @param replyId
     * @param content
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/update")
    public String replyUpdate(@RequestParam(name = "replyId") Long replyId,
            @RequestParam(name = "content") String content) {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyId(replyId)
                .content(content)
                .updateDate(LocalDateTime.now())
                .build();
        replyService.updateOne(replyDTO);
        return "";
    }

    // ================== 댓글 삭제 ==================

    /**
     * ajax - 댓글 삭제 처리 요청
     * 
     * @param replyId
     * @return
     */
    @ResponseBody
    @GetMapping("/replyl/delete")
    public String replyDelete(@RequestParam(name = "replyId") Long replyId) {
        replyService.deleteOne(replyId);
        return "";
    }

    // ================== 댓글 좋아요 ==================

    /**
     * ajax - 로그인한 사용자의 댓글 좋아요 여부 확인 후 좋아요 설정/해제 처리 요청
     * 
     * @param replyId
     * @param memberId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    @ResponseBody
    @GetMapping("/reply/likeUpdate")
    public boolean replyLikeUpdate(@RequestParam(name = "replyId") Long replyId,
            @RequestParam(name = "memberId") String memberId) {
        return replyService.toggleLikeOnReply(replyId, memberId);
    }

    /**
     * ajax - 입력받은 replyId에 해당하는 댓글의 좋아요 수 요청
     * 
     * @param replyId
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/getLikeCount")
    public int getLikeCount(@RequestParam(name = "replyId") Long replyId) {
        return replyService.getLikeCount(replyId);
    }

    // ================== 대댓글 작성 ==================

    @ResponseBody
    @GetMapping("/reply/createChild")
    public boolean replyCreateChild(@RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "parentReplyId") Long parentReplyId,
            @RequestParam(name = "memberId") String memberId,
            @RequestParam(name = "content") String content) {
        // 부모 댓글 존재 여부 확인
        if (replyService.existsParentReply(parentReplyId)) {
            // 대댓글 DTO 생성
            ReplyDTO replyDTO = ReplyDTO.builder()
                    .boardId(boardId)
                    .parentReplyId(parentReplyId)
                    .memberId(memberId)
                    .content(content)
                    .createDate(LocalDateTime.now())
                    .updateDate(null)
                    .likeCount(0)
                    .build();
            replyService.createOne(replyDTO); // 대댓글 저장 (기존 댓글 등록과 동일한 로직)
            return true;
        } else
            return false;
    }

}
