package net.dima_community.CommunityProject.entity;

import net.dima_community.CommunityProject.dto.MemberDTO;
import net.dima_community.CommunityProject.entity.member.MemberPageEntity;
import net.dima_community.CommunityProject.entity.member.MemberProjectEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder

@Entity
@Table(name = "Member")
public class MemberEntity {
	@Id
	@Column(name = "member_id")
	private String memberId;

	@Column(name = "member_pw", nullable = false)
	private String memberPw;

	@Column(name = "member_enabled", nullable = false)
	private String memberEnabled;

	@Column(name = "member_role", nullable = false)
	private String memberRole;

	@Column(name = "member_name", nullable = false)
	private String memberName;

	@Column(name = "member_email", nullable = false)
	private String memberEmail;

	@Column(name = "member_group", nullable = false)
	private String memberGroup;

	@Column(name = "member_phone", nullable = false)
	private String memberPhone;

	@Column(name = "badge1")
	private String badge1;

	@Column(name = "badge2")
	private String badge2;

	@Column(name = "verificationcode")
	private String memberVerifyCode;

	/*
	 * MemberPage와 관계 설정
	 */
	@OneToOne(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("memberpage_Seq asc")
	private MemberPageEntity memberPageEntity;

	/*
	 * MemberProject와 관계 설정
	 */
	@OneToMany(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("memberproject_seq asc")
	private List<MemberProjectEntity> memberProjectEntity;

	public static MemberEntity toEntity(MemberDTO memberDTO) {
		return MemberEntity.builder()
				.memberId(memberDTO.getMemberId())
				.memberPw(memberDTO.getMemberPw())
				.memberEnabled(memberDTO.getMemberEnabled())
				.memberRole(memberDTO.getMemberRole())
				.memberName(memberDTO.getMemberName())
				.memberEmail(memberDTO.getMemberEmail())
				.memberGroup(memberDTO.getMemberGroup())
				.memberPhone(memberDTO.getMemberPhone())
				.badge1(memberDTO.getBadge1())
				.badge2(memberDTO.getBadge2())
				.memberVerifyCode(memberDTO.getMemberVerifyCode())
				.build();
	}
}
