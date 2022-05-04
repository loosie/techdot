package com.techdot.techdot.modules.member;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.modules.member.dto.ProfileFormDto;

class MemberTest {


	@DisplayName("멤버 생성 오류 - 입력값 null인 경우")
	@Test
	void memberCreate_insertValueIsNull_ExceptionThrown() {
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname(null).password("test1234").email("jong9712@naver.com").emailVerified(false).build());
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname("loosie").password(null).email("jong9712@naver.com").emailVerified(false).build());
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname("loosie").password("test1234").email(null).emailVerified(false).build());
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname("loosie").password("test1234").email("jong9712@naver.com").emailVerified(null).build());
	}


	@DisplayName("회원 이메일 인증 토큰 정보 생성하기")
	@Test
	void memberEmailCheckTokenInfoCreate_EmailCheckTokenSendAtAndEmailSendTime_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.countEmailSendTime();

		// when, then
		assertNotNull(member.getEmailCheckTokenSendAt());
		assertEquals(member.getEmailSendTime(), 1);
	}


	@DisplayName("회원 이메일 인증 성공 - ROLE_MEMBER 권한 업데이트")
	@Test
	void memberCompleteEmailVerified_UpdateUserRoleToMember_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		member.completeEmailVerified();

		// then
		assertTrue(member.getEmailVerified());
		assertTrue(member.getRoles().contains(Role.ROLE_MEMBER));
	}

	@DisplayName("회원 인증 이메일 전송 횟수 카운트")
	@Test
	void memberCountEmailSendTime_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.countEmailSendTime();

		// when, then
		assertEquals(member.getEmailSendTime(), 1);
		assertTrue(member.canSendConfirmEmail());
		assertEquals(member.getEmailSendTime(), 2);
		assertFalse(member.canSendConfirmEmail());
	}

	@DisplayName("회원 프로필 업데이트하기")
	@Test
	void memberUpdateProfile_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		ProfileFormDto profileFormDto = new ProfileFormDto();
		profileFormDto.setBio("hi. i'm loosie");
		profileFormDto.setCurNickname("loosie");
		profileFormDto.setNewNickname("updateNickname");
		profileFormDto.setProfileImage("12345678...dataURL");

		// when
		member.updateProfile(profileFormDto);

		// then
		assertEquals(member.getBio(), "hi. i'm loosie");
		assertEquals(member.getNickname(), "updateNickname");
	}

	@DisplayName("회원 비밀번호 업데이트")
	@Test
	void memberUpdatePassword_Success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		member.updatePassword("87654321");

		// then
		assertTrue(member.getPassword().equals("87654321"));
	}

}