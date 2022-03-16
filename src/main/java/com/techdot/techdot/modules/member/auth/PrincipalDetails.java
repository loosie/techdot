package com.techdot.techdot.modules.member.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.techdot.techdot.modules.member.Member;

import lombok.Getter;

// @CurrentUser -> @AuthenticationPrincipal 인증된 정보있을 경우 'member' get
@Getter
public class PrincipalDetails implements UserDetails {

	private Member member;

	public PrincipalDetails(Member member){
		this.member = member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		member.getRoles().stream().forEach(role -> authorities.add((GrantedAuthority)() -> role.toString()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
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
