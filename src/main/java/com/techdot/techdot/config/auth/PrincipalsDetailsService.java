package com.techdot.techdot.config.auth;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalsDetailsService implements UserDetailsService {

	private final MemberRepo memberRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<Member> opMember = memberRepo.findByEmail(email);
		if(!opMember.isEmpty()){
			Member member = opMember.get();
			return new PrincipalDetails(member);
		}

		throw new UsernameNotFoundException(email);
	}
}
