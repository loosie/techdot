package com.techdot.techdot.modules.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import com.techdot.techdot.infra.config.AppProperties;
import com.techdot.techdot.infra.mail.EmailService;
import com.techdot.techdot.modules.member.auth.PrincipalDetails;
import com.techdot.techdot.modules.member.dao.AuthDao;
import com.techdot.techdot.modules.member.dto.PasswordFormDto;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	private MemberService memberService;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private EmailService emailService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private TemplateEngine templateEngine;
	@Mock
	private AppProperties appProperties;
	@Mock
	private AuthDao authDao;


	@BeforeEach
	void setUp() {
		memberService = new MemberService(memberRepository, emailService, passwordEncoder, templateEngine, appProperties, authDao);
	}

	@DisplayName("회원 로그인 성공")
	@Test
	void memberLogin__Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		memberService.login(member);

		// then
		assertEquals(SecurityContextHolder.getContext().getAuthentication().getName(), "jong9712@naver.com");
		assertTrue(member.getRoles().contains(Role.ROLE_USER));
		assertFalse(member.getRoles().contains(Role.ROLE_MEMBER));
	}

	@DisplayName("회원 전체 로그인 성공 - 이메일 인증 완료한 경우")
	@Test
	void memberCompleteLogin_UserDoEmailConfirm_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		memberService.completeLogin(member);

		// then
		assertTrue(member.getEmailVerified());
		assertTrue(member.getRoles().contains(Role.ROLE_MEMBER));
	}

	@DisplayName("회원 정보 변경하기 성공 - Nickname, Bio, ProfileImage")
	@Test
	void memberUpdateProfile_ChangeNicknameAndBioAndProfileImage_Success(){
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		ProfileFormDto profileFormDto = new ProfileFormDto();
		profileFormDto.setNewNickname("changeLoosie");
		profileFormDto.setProfileImage("image");
		profileFormDto.setBio("bio");
		given(memberRepository.save(member)).willReturn(member);

		// when
		memberService.updateProfile(member, profileFormDto);

		// then
		assertEquals(member.getNickname(), "changeLoosie");
		assertEquals(member.getProfileImage(), "image");
		assertEquals(member.getBio(), "bio");
	}

	@DisplayName("회원 비밀번호 수정하기 성공")
	@Test
	void memberUpdatePassword_Success(){
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		PasswordFormDto passwordFormDto = new PasswordFormDto();
		passwordFormDto.setNewPassword("changePassword");
		passwordFormDto.setNewPasswordConfirm("changePassword");
		given(memberRepository.save(member)).willReturn(member);
		given(passwordEncoder.encode(passwordFormDto.getNewPassword())).willReturn("encodePassword");

		// when
		memberService.updatePassword(member, passwordFormDto);

		// then
		assertEquals(member.getPassword(), "encodePassword");
	}

	@DisplayName("회원 탈퇴하기 성공")
	@Test
	void memberWithdrawal_Success(){
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new PrincipalDetails(member), member.getPassword()));
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());

		// when
		memberService.withdrawal(member);

		// then
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}


}