package com.techdot.techdot.controller;

import static com.techdot.techdot.controller.AccountsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.infra.WithCurrentUser;
import com.techdot.techdot.repository.MemberRepository;

@MockMvcTest
class AccountsControllerTest{

	@Autowired private MockMvc mockMvc;
	@Autowired private MemberRepository memberRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	private final String TEST_EMAIL = "test@naver.com";
	private final String TEST_NICKNAME = "testNickname";
	private final String REQUEST_URL = "/accounts";

	@AfterEach
	void end() {
		memberRepository.deleteAll();
	}


	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("개인정보 설정 메인 뷰")
	@Test
	void profileSettingsView() throws Exception {
		mockMvc.perform(get(REQUEST_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("프로필 수정하기 - 정상")
	@Test
	void updateProfile_success() throws Exception {
		String bio = "소개를 수정하는 경우";
		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_MAIN_VIEW_URL)
			.param("curNickname", TEST_NICKNAME)
			.param("newNickname", TEST_NICKNAME)
			.param("bio", bio)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(REQUEST_URL))
			.andExpect(flash().attributeExists("message"));

		// then
		Member findMember = memberRepository.findByNickname(TEST_NICKNAME).orElseThrow(NullPointerException::new);
		assertEquals(findMember.getBio(), bio);
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("프로필 수정하기 - 입력값 에러 - 소개글 범위 초과")
	@Test
	void updateProfile_error_wrongInput() throws Exception {
		String bio = "소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.";
		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_MAIN_VIEW_URL)
			.param("curNickname", TEST_NICKNAME)
			.param("newNickname", TEST_NICKNAME)
			.param("bio", bio)
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().hasErrors());

		// then
		Member findMember = memberRepository.findByNickname(TEST_NICKNAME).orElseThrow(NullPointerException::new);
		assertNull(findMember.getBio());
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("프로필 수정하기 - 입력값 에러 - 닉네임 중복")
	@Test
	void updateProfile_error_duplicatedNickname() throws Exception {
		// given
		Member newMember = memberRepository.save(Member.builder()
			.email("test2@naver.com")
			.password("12345678")
			.nickname("test")
			.emailVerified(true)
			.termsCheck(true)
			.build());

		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_MAIN_VIEW_URL)
			.param("curNickname", "test")
			.param("newNickname", TEST_NICKNAME)
			.param("bio", "bio")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().hasErrors());

		// then
		Member findMember = memberRepository.findByNickname(TEST_NICKNAME).orElseThrow(NullPointerException::new);
		assertFalse(findMember.equals(newMember));
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("비밀번호 변경 뷰")
	@Test
	void updatePasswordView() throws Exception {
		mockMvc.perform(get(REQUEST_URL +ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("passwordForm"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("비밀번호 변경하기 - 정상")
	@Test
	void updatePassword_success() throws Exception {
		String newPw = "1234567890";
		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_PASSWORD_VIEW_URL)
			.param("newPassword", newPw)
			.param("newPasswordConfirm", newPw)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(flash().attributeExists("message"));

		// then
		Member findMember = memberRepository.findByEmail(TEST_EMAIL).orElseThrow(NullPointerException::new);
		assertTrue(passwordEncoder.matches(newPw, findMember.getPassword()));
	}

	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("비밀번호 변경하기 - 입력값 에러")
	@Test
	void updatePassword_error_unMatchedPassword() throws Exception {
		String newPw = "1234567890";
		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_PASSWORD_VIEW_URL)
			.param("newPassword", newPw)
			.param("newPasswordConfirm", newPw + "abc")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PASSWORD_VIEW_NAME))
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("member"));
	}


	@WithCurrentUser(value = TEST_EMAIL, role ="MEMBER")
	@DisplayName("설정 뷰")
	@Test
	void accountsSettingView() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_SETTING_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_SETTING_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role="ADMIN")
	@DisplayName("카테고리 관리 뷰")
	@Test
	void accountsSettingCategoryView() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_CATEGORY_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_CATEGORY_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role="ADMIN")
	@DisplayName("게시글 관리 뷰")
	@Test
	void accountsMyUploadView() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_MY_UPLOAD_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_MY_UPLOAD_VIEW_NAME))
			.andExpect(model().attributeExists("postPage"))
			.andExpect(model().attributeExists("member"));
	}

}