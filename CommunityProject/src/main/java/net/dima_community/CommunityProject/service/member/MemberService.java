package net.dima_community.CommunityProject.service.member;

import java.lang.reflect.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

// import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.common.exception.ResourceNotFoundException;
import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;
import net.dima_community.CommunityProject.common.port.DBConnector;
import net.dima_community.CommunityProject.common.util.FileService;
import net.dima_community.CommunityProject.dto.member.MemberDTO;
import net.dima_community.CommunityProject.dto.member.MemberPageDTO;
import net.dima_community.CommunityProject.entity.member.MemberEntity;
import net.dima_community.CommunityProject.repository.member.MemberPageRepository;
import net.dima_community.CommunityProject.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberPageRepository memberPageRepository;
	public final BCryptEncoderHolder bCryptEncoderHolder;
	public final DBConnector dbConnector;
	// 첨부 파일 경로 요청
	@Value("${spring.servlet.multipart.location}")
	String uploadPath;

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

	public Optional<MemberEntity> findEntityById(String id) {
		return memberRepository.findById(id);
	}

	// public void saveMemberWithVerificationCode(MemberDTO memberDTO, MultipartFile
	// uploadFile, String verifyCode) {
	//
	// MemberDTO result = memberDTO.updateVerifyCode(verifyCode);
	// memberRepository.save(MemberEntity.toEntity(result));
	// }

	public boolean verifyMemberByCode(String to, String code) {
		Optional<MemberEntity> result = memberRepository.findByMemberEmail(to);
		if (result == null || !result.isPresent()) {
			throw new ResourceNotFoundException("Member", to);
		}
		MemberDTO member = MemberDTO.toDTO(result.get());
		// log.info(member.getMemberVerifyCode() + code);
		// if (!code.trim().equals(member.getMemberVerifyCode())) {
		// // memberRepository.delete(member);
		// return false;
		// } else {
		// memberPageRepository.save(member,
		// MemberPageDTO.builder().memberId(member.getMemberId()).build());
		// return true;
		// }
		return true;
	}

	@Transactional
	public MemberDTO setEncodedPassword(MemberDTO memberDTO) {
		memberDTO.setEncodedPassword(bCryptEncoderHolder);
		return memberDTO;
	}

	@Transactional
	public void approve(String id) {
		// findEntityById 매소드 하나 만듦!!
		// findById와 findEntityById 구별해서 쓰기!!
		MemberEntity memberEntity = findEntityById(id).get();
		memberEntity.setMemberEnabled("Y");
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
		// originalMember.setMemberVerifyCode(generatedString);
	}

	// public boolean updateEmailProcess(String memberId, String verificationCode) {
	// MemberEntity memberEntity = memberRepository.findById(memberId).get();
	// if (memberEntity.getMemberVerifyCode().equals(verificationCode)) {
	// // 이메일 변경은 진짜 저장 버튼 누를 때!!!!!
	// return true;
	// } else {
	// return false;
	// }
	// }

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

	// ======================== 프로필 사진 관련 ========================

	/**
	 * originalFileName, savedFileName을 멤버변수로 가지고 있는 객체
	 */
	private static class FileDetails {
		private final String originalFileName;
		private final String savedFileName;

		public FileDetails(String originalFileName, String savedFileName) {
			this.originalFileName = originalFileName;
			this.savedFileName = savedFileName;
		}

		public String getOriginalFileName() {
			return originalFileName;
		}

		public String getSavedFileName() {
			return savedFileName;
		}
	}

	/**
	 * uploadFile이 null이 아닌 경우 첨부파일을 저장하고, 원본파일명과 저장파일명이 담긴 FileDetails 객체 반환하는 함수
	 * 
	 * @param uploadFile
	 * @return FileDetails 객체
	 */
	private FileDetails handleFileUpload(MultipartFile uploadFile) {
		if (!uploadFile.isEmpty()) {
			String originalFileName = uploadFile.getOriginalFilename();
			String savedFileName = FileService.saveFile(uploadFile, uploadPath);
			return new FileDetails(originalFileName, savedFileName);
		}
		return null;
	}

	public void saveMember(MemberDTO memberDTO) {
		// 첨부파일이 있는 경우 파일 저장 후 DTO의 파일명 세팅
		memberDTO.setUploadFile(memberDTO.getUploadFile());
		FileDetails fileDetails = handleFileUpload(memberDTO.getUploadFile());
		if (fileDetails != null) {
			memberDTO.setOriginalFileName(fileDetails.getOriginalFileName());
			memberDTO.setSavedFileName(fileDetails.getSavedFileName());
		}
		memberRepository.save(MemberEntity.toEntity(memberDTO));
	}

	// 이미지 저장 경로 반환
	public String getFileFullPath(String fileName) {
		return uploadPath + "/" + fileName;
	}

	// ========== 프로필 사진 업데이트
	@Transactional
	public Boolean updateImage(String memberId, MultipartFile newImage) {
		MemberEntity memberEntity = findEntityById(memberId).get();
		log.info(memberEntity.toString());
		// 기존에 첨부파일이 있다면 삭제
		if (!(memberEntity.getOriginalFileName() == null)) {
			FileService.deleteFile(getFileFullPath(memberEntity.getSavedFileName()));
		}
		FileDetails fileDetails = handleFileUpload(newImage);
		memberEntity.setOriginalFileName(fileDetails.getOriginalFileName());
		memberEntity.setSavedFileName(fileDetails.getSavedFileName());
		memberRepository.save(memberEntity);
		return true;

	}

	public String showImageAtMain(String memberId) {
		MemberDTO memberDTO = findById(memberId);
		if (memberDTO.getSavedFileName() == null) {
			return getFileFullPath("user-1.jpg");
		} else {
			return getFileFullPath(memberDTO.getSavedFileName());
		}
	}

	@Transactional
	public Boolean deleteImage(String memberId) {
		log.info(memberId);
		MemberEntity memberEntity = findEntityById(memberId).get();
		if (!(memberEntity.getOriginalFileName() == null)) {
			FileService.deleteFile(getFileFullPath(memberEntity.getSavedFileName()));
			memberEntity.setOriginalFileName(null);
		}
		if (!(memberEntity.getSavedFileName() == null)) {
			memberEntity.setSavedFileName(null);
		}
		memberRepository.save(memberEntity);
		return true;
	}

	public void deleteMember(String memberId) {
		memberRepository.deleteById(memberId);
	}

}// end findmemId
