package net.dima_community.CommunityProject.dto;

import net.dima_community.CommunityProject.common.port.BCryptEncoderHolder;
import net.dima_community.CommunityProject.entity.MemberEntity;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class MemberDTO {
	private String memberId;
	private String memberPw;
	private String memberEnabled;
	private String memberRole;
	private String memberName;
	private String memberGroup;
	private String memberPhone;
	private String memberEmail;
	private String badge1;
	private String badge2;
	private MultipartFile uploadFile;
	private String originalFileName;
	private String savedFileName;
	public String memberVerifyCode;

	public static MemberDTO toDTO(MemberEntity memberEntity) {
		return MemberDTO.builder()
				.memberId(memberEntity.getMemberId())
				.memberPw(memberEntity.getMemberPw())
				.memberEnabled(memberEntity.getMemberEnabled())
				.memberRole(memberEntity.getMemberRole())
				.memberName(memberEntity.getMemberName())
				.memberGroup(memberEntity.getMemberGroup())
				.memberPhone(memberEntity.getMemberPhone())
				.memberEmail(memberEntity.getMemberEmail())
				.badge1(memberEntity.getBadge1())
				.badge2(memberEntity.getBadge2())
				.memberVerifyCode(memberEntity.getMemberVerifyCode())
				.build();
	}

	public MemberDTO updateVerifyCode(String verifyCode) {
		this.memberVerifyCode = verifyCode;
		return this;
	}

	public MemberDTO setEncodedPassword(BCryptEncoderHolder bCryptEncoderHolder) {
		this.memberPw = bCryptEncoderHolder.encodedPassword(this.getMemberPw());
		return this;
	}

	public void enabledToYes() {
		this.memberEnabled = Character.toString('Y');
	}

	public MemberDTO update(String memberName2, String memberEmail2) {
		this.memberName = memberName2;
		this.memberEmail = memberEmail2;
		return this;
	}

	public MemberDTO updateVerificationCode(String generatedString) {
		this.memberVerifyCode = generatedString;
		return this;
	};

	// 관리자페이지에 보여줄 회원
	public MemberDTO(String memberName, String memberEmail, String memberGroup, String memberId, String memberEnabled) {
		super();
		this.memberName = memberName;
		this.memberEmail = memberEmail;
		this.memberGroup = memberGroup;
		this.memberId = memberId;
		this.memberEnabled = memberEnabled;
	}

}
