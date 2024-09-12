package net.dima_community.CommunityProject.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.LoginMemberDetails;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.MemberRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginMemberDetailsService implements UserDetailsService {
	
	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		//userId 검증 로직이 필요. 테이블에 접근해서 데이터를 가져옴
		//사용자가 로그인을 하면 SecurityConfig가 userId을 여기로 전달함
		log.info("UserId : {}", memberId);
		
		MemberEntity memberEneity = memberRepository.findByMemberId(memberId)
				.orElseThrow(() -> {
					throw new UsernameNotFoundException("error 발생");
				});
		MemberDTO memberDTO = MemberDTO.toDTO(memberEneity);
		
		return new LoginMemberDetails(memberDTO);
	}
}
