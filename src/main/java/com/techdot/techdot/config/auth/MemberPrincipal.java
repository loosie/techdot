package com.techdot.techdot.config.auth;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.techdot.techdot.domain.Member;

import lombok.Getter;

// @CurrentUser -> @AuthenticationPrincipal 인증된 정보있을 경우 'member' get
@Getter
public class MemberPrincipal extends User {

	private Member member;

	public MemberPrincipal(Member member){
		super(member.getNickname(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		this.member = member;
	}

}
