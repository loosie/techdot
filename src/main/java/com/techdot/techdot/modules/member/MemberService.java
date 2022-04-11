package com.techdot.techdot.modules.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.techdot.techdot.infra.config.AppProperties;
import com.techdot.techdot.modules.interest.InterestRepository;
import com.techdot.techdot.modules.like.LikeRepository;
import com.techdot.techdot.modules.member.auth.PrincipalDetails;
import com.techdot.techdot.modules.member.dto.JoinFormDto;
import com.techdot.techdot.modules.member.dto.PasswordFormDto;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;
import com.techdot.techdot.modules.main.UserNotExistedException;
import com.techdot.techdot.infra.mail.EmailMessageDto;
import com.techdot.techdot.infra.mail.EmailService;

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

	// delete
	private final LikeRepository likeRepository;
	private final InterestRepository interestRepository;

	/**
	 * 회원가입
	 * -> 멤버 저장하기
	 * -> 인증 메일 전송
	 */
	public Member save(final JoinFormDto joinForm) {
		Member newMember = saveMember(joinForm);
		sendConfirmEmail(newMember);
		return newMember;
	}

	/**
	 * 인증 메일 전송하기
	 */
	public void sendConfirmEmail(final Member newMember) {
		Context context = new Context();
		context.setVariable("link",
			"/confirm-email?token=" + newMember.getEmailCheckToken() + "&email=" + newMember.getEmail());
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

	/**
	 * 멤버 저장하기
	 */
	private Member saveMember(final JoinFormDto joinForm) {
		Member member = Member.builder()
			.email(joinForm.getEmail())
			.nickname(joinForm.getNickname())
			.password(passwordEncoder.encode(joinForm.getPassword()))
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();
		return memberRepository.save(member);
	}

	/**
	 * 로그인 완료
	 * -> 이메일 인증 완료 처리
	 * -> SecurityContextHolder에 멤버 세션 저장하여 최종 로그인 처리
	 */
	public void completeLogin(final Member member) {
		member.completeEmailVerified();
		login(member);
	}

	/**
	 * Security 세션에 멤버 정보 등록하여 로그인
	 */
	public void login(final Member member) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		member.getRoles().stream().forEach(role -> authorities.add((GrantedAuthority)() -> role.toString()));

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			new PrincipalDetails(member),
			member.getPassword(),
			authorities);
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	/**
	 * 멤버 프로필 업데이트
	 * nickname, bio, profileImage
	 */
	public void updateProfile(final Member member, final ProfileFormDto profileForm) {
		member.updateProfile(profileForm);
		memberRepository.save(member);
	}

	/**
	 * 멤버 비밀번호 업데이트
	 */
	public void updatePassword(final Member member, final PasswordFormDto passwordForm) {
		member.updatePassword(passwordEncoder.encode(passwordForm.getNewPassword()));
		memberRepository.save(member);
	}

	/**
	 * 로그인 링크 이메일로 전송하기
	 */
	public void sendLoginLink(final Member member) {
		Context context = new Context();
		context.setVariable("link",
			"/login-by-email?token=" + member.getEmailCheckToken() + "&email=" + member.getEmail());
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

	/**
	 * email로 멤버 조회하기
	 */
	public Member getByEmail(final String email, final String redirectView) {
		Optional<Member> opMember = memberRepository.findByEmail(email);
		if (opMember.isEmpty()) {
			throw new UserNotExistedException(email + "은 유효한 이메일이 아닙니다.", redirectView);
		}
		return opMember.get();
	}

	public void withdrawal(Member member) {
		try {
			Long id = member.getId();
			likeRepository.deleteAllByMemberId(id);
			interestRepository.deleteAllByMemberId(id);
			memberRepository.delete(member);

			SecurityContextHolder.getContext().setAuthentication(null);
			log.info(member.getEmail() + " 회원 탈퇴가 정상적으로 처리되었습니다. ");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}
}
