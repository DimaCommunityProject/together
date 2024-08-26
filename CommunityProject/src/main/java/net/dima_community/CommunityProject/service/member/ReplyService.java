package net.dima_community.CommunityProject.service.member;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.dto.board.ReplyDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.entity.board.ReplyEntity;
import net.dima_community.CommunityProject.repository.member.ReplyRepository;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;

    public List<ReplyDTO> findByMemberId(MemberEntity memberEntity) {
        List<ReplyEntity> result = replyRepository.findByMemberId(memberEntity);
        return result.stream()
                .map(entity -> ReplyDTO.toDTO(entity, entity.getBoardEntity().getBoardId(), memberEntity.getMemberId()))
                .collect(Collectors.toList());

    }
}
