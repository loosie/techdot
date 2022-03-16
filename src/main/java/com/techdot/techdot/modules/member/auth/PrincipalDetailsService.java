package com.techdot.techdot.modules.member.auth;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Member> opMember = memberRepository.findByEmail(email);
		if (opMember.isEmpty()) {
			throw new UsernameNotFoundException(email);
		}

		Member member = opMember.get();
		member.updateEmailCheckToken(); // 로그인할 때마다 token 값 갱신
		return new PrincipalDetails(member);

	}
}
