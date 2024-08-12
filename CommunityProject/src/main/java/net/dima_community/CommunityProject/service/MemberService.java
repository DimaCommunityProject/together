package net.dima_community.CommunityProject.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	/**
	 * 회원가입
	 * @param memberDTO
	 */
	public boolean joinProc(MemberDTO memberDTO) {
		boolean isExistUser = memberRepository.existsById(memberDTO.getMemberId());
		
		if(isExistUser) return false;	//존재하는 아이디이므로 가입실패
		memberDTO.setMemberPw(bCryptPasswordEncoder.encode(memberDTO.getMemberPw()));	//아이디가 존재하지않으면 비번 꺼내서 암호화 시킴
		
		// DTO를 Entity로 변경
		MemberEntity entity = MemberEntity.toEntity(memberDTO);
		memberRepository.save(entity);
		return true;
	}
	
	//controller에서 온 id찾기
		public Boolean findId(String memberId) {
			Optional<MemberEntity> entity = memberRepository.findByMemberId(memberId);
			log.info("엔티티는 뭔가요? : {}", entity);
			
			if(entity == null) {	//id가 없으면
				return true;
			}
			else {return false;}
		}
		
	/**
	 * 사용자 아이디 찾기
	 * @param memberName
	 * @param memberEmail
	 * @return
	 */
	public String findmemId(String memberName, String memberEmail) {
		String id = memberRepository.findIdByMemberNameAndMemberEmail(memberName, memberEmail);
		
		log.info("사용자 id 레퍼지토리에서 찾아옴 : {}", id);
		return id;
	}
	
	//사용자가 맞는지 확인
//	public int PwCheck(String memberEmail, String memberId) {
//	    
//		int pw = memberRepository.findIdByMemberEmailAndMemberId(memberEmail, memberId);
//		
//		log.info("사용자 확인 레퍼지토리에서 찾아옴 : {}", pw);
//		return pw;
//	}
	
	public int PwCheck(MemberDTO memberDTO) {
	    
		int pw = memberRepository.findIdByMemberEmailAndMemberId(memberDTO.getMemberEmail(), memberDTO.getMemberId());
		
		log.info("사용자 확인 레퍼지토리에서 찾아옴 : {}", pw);
		return pw;
	}
	
	//임시비번 암호화 후 업뎃
	public void PwUpdate(MemberDTO memberDTO) {
		String newPwUpdate = bCryptPasswordEncoder.encode(memberDTO.getMemberPw());	//임시비번 암호화
		memberDTO.setMemberPw(newPwUpdate);		
		memberRepository.PwUpdate(memberDTO.getMemberId(), newPwUpdate);			//업뎃
	}//end findmemId
}
