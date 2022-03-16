package com.techdot.techdot.modules.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.techdot.techdot.infra.config.AppProperties;
import com.techdot.techdot.modules.member.auth.PrincipalDetails;
import com.techdot.techdot.modules.member.dto.JoinFormDto;
import com.techdot.techdot.modules.member.dto.PasswordFormDto;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;
import com.techdot.techdot.infra.exception.UserNotExistedException;
import com.techdot.techdot.infra.mail.EmailMessageDto;
import com.techdot.techdot.infra.mail.EmailService;
import com.techdot.techdot.modules.post.Post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final TemplateEngine templateEngine;
	private final AppProperties appProperties;

	public Member save(JoinFormDto joinForm) {
		Member newMember = saveMember(joinForm);
		sendConfirmEmail(newMember);
		return newMember;
	}

	public void sendConfirmEmail(Member newMember) {
		Context context = new Context();
		context.setVariable("link", "/confirm-email?token=" + newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
		context.setVariable("nickname", newMember.getNickname());
		context.setVariable("linkName", "이메일 인증하기");
		context.setVariable("message", "테크닷 서비스를 사용하려면 인증 링크를 클릭해주세요.");
		context.setVariable("host", appProperties.getHost());

		String message = templateEngine.process("mail/confirm-email", context);

		EmailMessageDto emailMessageDto = EmailMessageDto.builder()
			.to(newMember.getEmail())
			.subject("Techdot 회원가입 이메일 인증을 확인해주세요")
			.message(message)
			.sendTime(newMember.getEmailSendTime())
			.build();
		emailService.sendEmail(emailMessageDto);
	}

	private Member saveMember(JoinFormDto joinForm) {
		Member member = Member.builder()
			.email(joinForm.getEmail())
			.nickname(joinForm.getNickname())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();
		return memberRepository.save(member);
	}

	public void completeLogin(Member member) {
		member.completeEmailVerified();
		login(member);
	}

	public void login(Member member) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		member.getRoles().stream().forEach(role -> authorities.add((GrantedAuthority)() -> role.toString()));

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			new PrincipalDetails(member),
			member.getPassword(),
			authorities);
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public void updateProfile(Member member, ProfileFormDto profileForm) {
		member.updateProfile(profileForm);
		memberRepository.save(member);
	}

	public void updatePassword(Member member, PasswordFormDto passwordForm) {
		member.updatePassword(passwordEncoder.encode(passwordForm.getNewPassword()));
		memberRepository.save(member);
	}

	public void sendLoginLink(Member member) {
		Context context = new Context();
		context.setVariable("link", "/login-by-email?token=" + member.getEmailCheckToken() + "&email=" + member.getEmail());
		context.setVariable("nickname", member.getNickname());
		context.setVariable("linkName", "이메일로 로그인하기");
		context.setVariable("message", "로그인하려면 인증 링크를 클릭해주세요.");
		context.setVariable("host", appProperties.getHost());
		String message = templateEngine.process("mail/confirm-email", context);

		EmailMessageDto emailMessageDto = EmailMessageDto.builder()
			.to(member.getEmail())
			.subject("Techdot 로그인 이메일 인증을 확인해주세요")
			.message(message)
			.sendTime(member.getEmailSendTime())
			.build();
		emailService.sendEmail(emailMessageDto);
	}

	public Member findByEmail(String email, String redirectView) {
		Optional<Member> opMember = memberRepository.findByEmail(email);
		if (opMember.isEmpty()) {
			throw new UserNotExistedException(email + "은 유효한 이메일이 아닙니다.", redirectView);
		}
		return opMember.get();
	}
}
