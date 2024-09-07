package net.dima_community.CommunityProject.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.entity.member.MemberEntity;
import net.dima_community.CommunityProject.repository.board.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberDTO findById(String id) {
		Optional<MemberEntity> result = memberRepository.findById(id);
		if (result == null | !result.isPresent()) {
			throw new ResourceNotFoundException("Member", id);
		}
		return MemberDTO.toDTO(result.get());
		// return memberRepository.findById(id).orElseThrow(() -> new
		// ResourceNotFoundException("Member", id));
	}

}
