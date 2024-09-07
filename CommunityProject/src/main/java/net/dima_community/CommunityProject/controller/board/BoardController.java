package net.dima_community.CommunityProject.controller.board;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.service.board.BoardService;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/showBoard")
    @ResponseBody
    public List<BoardDTO> showBoard(@RequestParam(name = "memberId") String memberId) {
        List<BoardDTO> result = boardService.findByUsername(memberId);
        return result;
    }
}
