package net.dima_community.CommunityProject.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.member.MemberDTO;

@Slf4j
public class LoginMemberDetails implements UserDetails {

	private String memberId;
	private String memberPw;
	private String memberRole;
	private String memberGroup;
	private String memberEmail;
	
	private static final long serialVersionUID = 1L; // 자동으로 발생

	public LoginMemberDetails(MemberDTO memberDTO) {
		super();
		this.memberId = memberDTO.getMemberId();
		this.memberPw = memberDTO.getMemberPw();
		this.memberRole = memberDTO.getMemberRole();
		this.memberGroup = memberDTO.getMemberGroup();
		this.memberEmail = memberDTO.getMemberEmail();
	}
	
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
	
	// 추가: memberRole 반환 메서드
    public String getRole() {
        return this.memberRole;
    }
    
    public String getGroup() {
    	log.info("Group data: {}", this.memberGroup);
    	return this.memberGroup;
    }
    
    public String getEmail() {
    	log.info("email data: {}", this.memberEmail);
    	return this.memberEmail;
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
