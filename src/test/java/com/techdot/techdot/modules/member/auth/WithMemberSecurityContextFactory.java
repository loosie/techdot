package com.techdot.techdot.modules.member.auth;

import static com.techdot.techdot.infra.Constant.*;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberService;
import com.techdot.techdot.modules.member.Role;
import com.techdot.techdot.modules.member.dto.JoinFormDto;

public class WithMemberSecurityContextFactory implements WithSecurityContextFactory<WithCurrentUser> {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private MemberService memberService;

	@Override
	public SecurityContext createSecurityContext(WithCurrentUser withCurrentUser) {
		String email = withCurrentUser.value();
		Member member = createMember(email);

		UserDetails principal = userDetailsService.loadUserByUsername(email);
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)principal.getAuthorities();

		authorities.add((GrantedAuthority)() -> Role.ROLE_USER.toString());
		if(withCurrentUser.role().equals(MEMBER) || withCurrentUser.role().equals(ADMIN)) {
			member.completeEmailVerified();
			authorities.add((GrantedAuthority)() -> Role.ROLE_MEMBER.toString());
		}

		if(withCurrentUser.role().equals(ADMIN)) {
			authorities.add((GrantedAuthority)() -> Role.ROLE_ADMIN.toString());
		}
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), authorities);
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}

	private Member createMember(String email) {
		JoinFormDto joinFormDto = new JoinFormDto();
		joinFormDto.setNickname("testNickname");
		joinFormDto.setEmail(email);
		joinFormDto.setPassword("12345678");
		return memberService.save(joinFormDto);

	}
}
