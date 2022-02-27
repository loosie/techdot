package com.techdot.techdot.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.JoinFormDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepo memberRepo;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void save(JoinFormDto joinForm) {
		Member newMember = saveMember(joinForm);
		newMember.generateEmailCheckToken();
		sendConfirmEmail(newMember);
	}

	private void sendConfirmEmail(Member newMember) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newMember.getEmail());
		mailMessage.setSubject("TechDot 이메일 인증");
		mailMessage.setText("/email-confirm?token=" + newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
		javaMailSender.send(mailMessage);
	}

	private Member saveMember(JoinFormDto joinForm) {
		Member member = Member.builder()
			.email(joinForm.getEmail())
			.nickname(joinForm.getNickname())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.termsCheck(joinForm.getTermsCheck())
			.build();

		return memberRepo.save(member);
	}

}
