package net.dima_community.CommunityProject.controller.board;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.service.member.MemberService;
import net.dima_community.CommunityProject.service.member.BoardService;
import net.dima_community.CommunityProject.service.member.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService ReplyService;
    private final BoardService boardService;
    private final MemberService memberService;

    @PostMapping("/showReply")
    @ResponseBody
    public Map<String, BoardDTO> showReply(@RequestParam(name = "memberId") String memberId) {
        MemberDTO member = memberService.findById(memberId);
        List<ReplyDTO> replyList = ReplyService.findByMemberId(MemberEntity.toEntity(member));
        Map<String, BoardDTO> dataMap = new HashMap<>();
        for (ReplyDTO temp : replyList) {
            BoardDTO board = boardService.findById(temp.getBoardId());
            dataMap.put(temp.getContent(), board);
        }
        return dataMap;
    }
}
