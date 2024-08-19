package net.dima_community.CommunityProject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
	private final MemberRepository memberRepository;
	
	@Value("${admin.page.pageLimit}")
	int pageLimit;
	
	//승인된 회원 목록 가져오기
	public Page<MemberDTO> selectEnableAll(Pageable pageable, String memberGroup) {
		int page = pageable.getPageNumber() -1;	//사용자가 요청한 페이지 번호. default 1
		
		Page<MemberEntity> entityList = null;
		
		entityList = memberRepository.findByMemberGroup(memberGroup, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "memberName")));
		
		log.info("관리자 페이지 회원 테이블(서비스단) : {}", entityList.getContent());
		
		Page<MemberDTO> dtoList = null;
		
		//보여줄 내용만 추려서 생성
		dtoList = entityList.map(member ->
		new MemberDTO(member.getMemberName(),
				member.getMemberEmail(),
				member.getMemberGroup(),
				member.getMemberId(),
				member.getMemberEnabled()));
		return dtoList;
	}//end selectAll
	
	//승인 안된 회원 목록 가져오기
	public List<MemberDTO> selectDisableAll() {
		
		List<MemberEntity> entityList = null;
		entityList = memberRepository.findByMemberEnabled("n");
		
		//log.info("관리자 페이지 회원 테이블(서비스단) : {}", entityList.getContent());
		
		List<MemberDTO> dtoList = null;
		
		//보여줄 내용만 추려서 생성
		 return entityList.stream()
                 .map(member -> new MemberDTO(
                	 member.getMemberName(),
                	 member.getMemberEmail(),
                	 member.getMemberGroup(),
                     member.getMemberId(),
                     member.getMemberEnabled()))
                 .collect(Collectors.toList());
	}//end select
}
