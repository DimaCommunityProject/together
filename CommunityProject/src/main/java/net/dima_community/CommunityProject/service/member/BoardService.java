package net.dima_community.CommunityProject.service.member;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.board.BoardEntity;
import net.dima_community.CommunityProject.repository.member.MemberRepository;
import net.dima_community.CommunityProject.repository.board.BoardRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public List<BoardDTO> findByUsername(String memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).get();
        List<BoardDTO> result = boardRepository.findByMemberId(memberEntity).stream()
                .map(entity -> BoardDTO.toDTO(entity, memberEntity.getMemberId()))
                .collect(Collectors.toList());
        return result;
    }

    public BoardDTO findById(Long boardId) {
        // log.info("+++++++++++++++++++++++++" + boardId);
        BoardEntity result = boardRepository.findById(boardId).get();
        log.info(result.toString());
        return BoardDTO.toDTO(result, result.getMemberEntity().getMemberId());
    }

}
