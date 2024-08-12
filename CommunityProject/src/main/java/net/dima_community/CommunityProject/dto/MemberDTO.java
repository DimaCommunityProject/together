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
			.build();
	}


}
