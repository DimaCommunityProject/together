package net.dima_community.CommunityProject.service.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;
import net.dima_community.CommunityProject.common.port.DBConnector;
import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.MemberEntity;
import net.dima_community.CommunityProject.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;

	public final BCryptEncoderHolder bCryptEncoderHolder;
	public final DBConnector dbConnector;

	/**
	 * 회원가입
	 * 
	 * @param memberDTO
	 */

	// controller에서 온 id찾기
	// SQL Injection 대비 PreparedStatement 사용
	public boolean findByIdThroughConn(String id) {
		Connection conn = dbConnector.getConnection();
		if (conn != null)
			log.info("conn 생성");

		String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return false; // 이미 있는 아이디
			}
			return true; // 사용 가능한 아이디

		} catch (SQLException e) {
			log.info("SQLException 발생!");
			e.printStackTrace();
			return false;
		}
	}

	public MemberDTO findById(String id) {
		Optional<MemberEntity> result = memberRepository.findById(id);
		if (result == null | !result.isPresent()) {
			throw new ResourceNotFoundException("Member", id);
		}
		return MemberDTO.toDTO(result.get());
		// return memberRepository.findById(id).orElseThrow(() -> new
		// ResourceNotFoundException("Member", id));
	}

	public void saveMemberWithVerificationCode(MemberDTO memberDTO, String verifyCode) {
		MemberDTO result = memberDTO.updateVerifyCode(verifyCode);
		memberRepository.save(MemberEntity.toEntity(result));
	}

	public boolean verifyMemberByCode(String to, String code) {
		Optional<MemberEntity> result = memberRepository.findByMemberEmail(to);
		if (result == null || !result.isPresent()) {
			throw new ResourceNotFoundException("Member", to);
		}
		MemberDTO member = MemberDTO.toDTO(result.get());
		if (!code.equals(member.getMemberVerifyCode())) {
			// memberRepository.delete(member);
			return false;
		}
		return true;
	}

	@Transactional
	public MemberDTO setEncodedPassword(MemberDTO memberDTO) {
		memberDTO.setEncodedPassword(bCryptEncoderHolder);
		return memberDTO;
	}

	public void approve(String id) {
		MemberDTO memberDTO = findById(id);
		memberDTO.enabledToYes();
		log.info(memberDTO.toString());
		memberRepository.save(MemberEntity.toEntity(memberDTO));
	}

	public MemberDTO updateMember(String memberId, String memberName, String memberEmail) {
		MemberDTO member = null;
		// try {
		log.info("updateMember 도착");
		member = findById(memberId);
		// } catch (ResourceNotFoundException e) {
		// return member;
		// }
		MemberDTO updatedMember = member.update(memberName, memberEmail);
		memberRepository.save(MemberEntity.toEntity(updatedMember));
		return updatedMember;
	}

	/**
	 * 사용자 아이디 찾기
	 * 
	 * @param memberName
	 * @param memberEmail
	 * @return
	 */
	public String findmemId(String memberName, String memberEmail) {
		String id = memberRepository.findIdByMemberNameAndMemberEmail(memberName, memberEmail);

		log.info("사용자 id 레퍼지토리에서 찾아옴 : {}", id);
		return id;
	}

	// 사용자가 맞는지 확인
	// public int PwCheck(String memberEmail, String memberId) {
	//
	// int pw = memberRepository.findIdByMemberEmailAndMemberId(memberEmail,
	// memberId);
	//
	// log.info("사용자 확인 레퍼지토리에서 찾아옴 : {}", pw);
	// return pw;
	// }

	public int PwCheck(MemberDTO memberDTO) {

		int pw = memberRepository.findIdByMemberEmailAndMemberId(memberDTO.getMemberEmail(), memberDTO.getMemberId());

		log.info("사용자 확인 레퍼지토리에서 찾아옴 : {}", pw);
		return pw;
	}

	@Transactional
	public void updateVerificationCode(String memberId, String generatedString) {

		MemberEntity originalMember = memberRepository.findById(memberId).get();
		log.info(generatedString);
		originalMember.setMemberVerifyCode(generatedString);
	}

	public boolean updateEmailProcess(String memberId, String verificationCode) {
		MemberEntity memberEntity = memberRepository.findById(memberId).get();
		if (memberEntity.getMemberVerifyCode().equals(verificationCode)) {
			// 이메일 변경은 진짜 저장 버튼 누를 때!!!!!
			return true;
		} else {
			return false;
		}
	}

	@Transactional
	public void updateEmail(String memberId, String memberEmail) {
		MemberEntity memberEntity = memberRepository.findById(memberId).get();
		memberEntity.setMemberEmail(memberEmail);
	}

	// 임시비번 암호화 후 업뎃
	public boolean PwUpdate(MemberDTO memberDTO) {
		// String newPwUpdate = bCryptEncoderHolder.encode(memberDTO.getMemberPw()); //
		// 임시비번 암호화
		// memberDTO.setMemberPw(newPwUpdate);
		int result = memberRepository.PwUpdate(memberDTO.getMemberId(), memberDTO.getMemberPw());
		// 업뎃

		return result > 0;
	}
}// end findmemId
