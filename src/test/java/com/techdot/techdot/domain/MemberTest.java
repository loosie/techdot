package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.dto.ProfileFormDto;

class MemberTest {

	@DisplayName("멤버 생성하기 - 성공")
	@Test
	void member_create_success() {
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		assertEquals(member.getNickname(), "loosie");
	}

	@DisplayName("멤버 생성 실패 - 입력값 오류 password null")
	@Test
	void member_create_fail_nullValue() {
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname("loosie") // password null
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build());
	}

	@DisplayName("멤버 생성 실패 - 입력값 오류 termsCheck 값은 항상 true이어야 합니다.")
	@Test
	void member_create_fail_invalidInput() {
		assertThrows(IllegalArgumentException.class, () -> Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(false) // must be true
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build());
	}

	@DisplayName("멤버 이메일 체크 토큰 생성 - 성공")
	@Test
	void member_generateEmailCheckToken_success() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();

		// when, then
		assertNotNull(member.getEmailCheckToken());
		assertNotNull(member.getEmailCheckTokenSendAt());
		assertEquals(member.getEmailSendTime(), 1);
	}

	@DisplayName("멤버 이메일 인증 토큰 업데이트하기")
	@Test
	void member_updateEmailCheckToken() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();
		String firstToken = member.getEmailCheckToken();

		// when
		member.updateEmailCheckToken();

		// then
		assertNotEquals(firstToken, member.getEmailCheckToken());
	}

	@DisplayName("멤버 이메일 인증 완료하기")
	@Test
	void member_completeEmailVerified() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		member.completeEmailVerified();

		// then
		assertTrue(member.getEmailVerified());
	}

	@DisplayName("멤버 확인 이메일 전송하기")
	@Test
	void member_sendConfirmEmail() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();

		// when, then
		assertEquals(member.getEmailSendTime(), 1);
		assertTrue(member.canSendConfirmEmail());
		assertEquals(member.getEmailSendTime(), 2);
		assertFalse(member.canSendConfirmEmail());
	}

	@DisplayName("멤버 프로필 업데이트하기")
	@Test
	void member_updateProfile() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
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

	@DisplayName("멤버 비밀번호 업데이트하기")
	@Test
	void member_updatePassword() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		// when
		member.updatePassword("87654321");

		// then
		assertTrue(member.getPassword().equals("87654321"));
	}

	@DisplayName("멤버 토큰 유효한지 검사하기")
	@Test
	void member_has_a_validToken() {
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		member.generateEmailCheckToken();

		// when, then
		assertTrue(member.isValidToken(member.getEmailCheckToken()));
		assertFalse(member.isValidToken(member.getEmailCheckToken() + "11"));
	}
}