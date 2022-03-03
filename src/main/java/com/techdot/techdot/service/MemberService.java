package com.techdot.techdot.service;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.config.auth.PrincipalDetails;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.JoinFormDto;
import com.techdot.techdot.dto.PasswordFormDto;
import com.techdot.techdot.dto.ProfileFormDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepo memberRepo;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;

	public Member save(JoinFormDto joinForm) {
		Member newMember = saveMember(joinForm);
		// newMember.generateEmailCheckToken();
		sendConfirmEmail(newMember);
		return newMember;
	}

	public void sendConfirmEmail(Member newMember) {
		log.info("send confirm email : {} ", + newMember.getEmailSendTime());
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newMember.getEmail());
		mailMessage.setSubject("TechDot 이메일 인증" );
		mailMessage.setText(
			"/confirm-email?token=" + newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
		javaMailSender.send(mailMessage);
	}

	private Member saveMember(JoinFormDto joinForm) {
		Member member = Member.builder()
			.email(joinForm.getEmail())
			.nickname(joinForm.getNickname())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.termsCheck(joinForm.getTermsCheck())
			.build();
		member.generateEmailCheckToken();
		return memberRepo.save(member);
	}

	public void completeLogin(Member member) {
		member.completeEmailVerified();
		login(member);
	}

	public void login(Member member) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			new PrincipalDetails(member),
			member.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	public void updateProfile(Member member, ProfileFormDto profileForm) {
		member.updateProfile(profileForm);
		memberRepo.save(member);
	}

	public void updatePassword(Member member, PasswordFormDto passwordForm) {
		member.updatePassword(passwordEncoder.encode(passwordForm.getNewPassword()));
		memberRepo.save(member);

	}

	public void sendLoginLink(Member member) {
		log.info("send login email : {} ", + member.getEmailSendTime());
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(member.getEmail());
		mailMessage.setSubject("TechDot 이메일 인증" );
		mailMessage.setText(
			"/login-by-email?token=" + member.getEmailCheckToken() + "&email=" + member.getEmail());
		javaMailSender.send(mailMessage);
	}
}
