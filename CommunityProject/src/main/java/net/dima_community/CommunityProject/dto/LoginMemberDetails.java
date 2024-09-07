package net.dima_community.CommunityProject.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import net.dima_community.CommunityProject.dto.member.MemberDTO;

public class LoginMemberDetails implements UserDetails {

	private String memberId;
	private String memberPw;
	private String memberRole;
	private static final long serialVersionUID = 1L; // 자동으로 발생

	public LoginMemberDetails(MemberDTO memberDTO) {
		super();
		this.memberId = memberDTO.getMemberId();
		this.memberPw = memberDTO.getMemberPw();
		this.memberRole = memberDTO.getMemberRole();
	}

	// @Override
	// //현재 사용자가 가지고 있는 권한(authorities) 목록을 반환하는 데 사용.
	// public Collection<? extends GrantedAuthority> getAuthorities() {
	// Collection <GrantedAuthority> collection = new ArrayList<>();
	// collection.add(new GrantedAuthority() {
	// private static final long serialVersionUID = 1L;
	//
	// @Override
	// public String getAuthority() {
	//
	// return memberRole;
	// }
	// });
	// return collection;
	// }

	@Override
	// 현재 사용자가 가지고 있는 권한(authorities) 목록을 반환하는 데 사용.
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		// SimpleGrantedAuthority를 사용하여 권한을 설정합니다.
		collection.add(new SimpleGrantedAuthority(memberRole)); // 'ROLE_ADMIN' 등의 권한이 그대로 추가됨
		return collection;
	}

	@Override
	public String getPassword() {
		return this.memberPw;
	}

	@Override
	public String getUsername() {
		return this.memberId;
	}

	@Override
	public boolean isAccountNonExpired() {

		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return true;
	}

	@Override
	public boolean isEnabled() {

		return true;
	}

}
