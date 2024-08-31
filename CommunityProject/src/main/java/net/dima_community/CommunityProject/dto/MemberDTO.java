package net.dima_community.CommunityProject.dto;

import net.dima_community.CommunityProject.entity.MemberEntity;

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
	private Long memberNum;
	private String memberId;
	private String memberPw;
	private Boolean memberEnabled;
	private String memberRole;
	private String memberName;
	private String memberGroup;
	private String memberPhone;
	private String memberEmail;
	private String badge1;
	private String badge2;
	private String memberGit;
	private String memberBlog;
	private String memberResume;
	
	public static MemberDTO toDTO(MemberEntity memberEntity) {
		return MemberDTO.builder()
			.memberNum(memberEntity.getMemberNum())
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
			.memberGit(memberEntity.getMemberGit())
			.memberBlog(memberEntity.getMemberBlog())
			.memberResume(memberEntity.getMemberResume())
			.build();
	}


}
