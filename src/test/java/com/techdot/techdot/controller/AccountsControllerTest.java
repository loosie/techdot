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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.auth.WithCurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String TEST_EMAIL = "test@naver.com";
	private final String TEST_NICKNAME = "loosie";

	@AfterEach
	void end() {
		memberRepo.deleteAll();
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("프로필 뷰")
	@Test
	void profileForm() throws Exception {
		mockMvc.perform(get("/" + TEST_NICKNAME))
			.andExpect(status().isOk())
			.andExpect(view().name(MEMBER_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("profile"))
			.andExpect(model().attributeExists("isOwner"));
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("프로필 설정 뷰")
	@Test
	void profileSettingsView() throws Exception {
		mockMvc.perform(get(ACCOUNTS_MAIN_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}


	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("프로필 수정하기 - 정상")
	@Test
	void updateProfile_success() throws Exception {
		String bio = "소개를 수정하는 경우";
		mockMvc.perform(post(ACCOUNTS_MAIN_VIEW_URL)
			.param("nickname", TEST_NICKNAME)
			.param("bio", bio)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(ACCOUNTS_MAIN_VIEW_URL))
			.andExpect(flash().attributeExists("message"));

		// then
		Member findMember = memberRepo.findByNickname("loosie").orElseThrow(NullPointerException::new);
		assertEquals(findMember.getBio(), bio);
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("프로필 수정하기 - 입력값 에러")
	@Test
	void updateProfile_error_wrongInput() throws Exception {
		String bio = "소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.";
		mockMvc.perform(post(ACCOUNTS_MAIN_VIEW_URL)
			.param("nickname", TEST_NICKNAME)
			.param("bio", bio)
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().hasErrors());

		// then
		Member findMember = memberRepo.findByNickname(TEST_NICKNAME).orElseThrow(NullPointerException::new);
		assertNull(findMember.getBio());
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("비밀번호 변경 뷰")
	@Test
	void updatePasswordView() throws Exception {
		mockMvc.perform(get(ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("passwordForm"));
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("비밀번호 변경하기 - 정상")
	@Test
	void updatePassword_success() throws Exception {
		String newPw = "1234567890";
		mockMvc.perform(post(ACCOUNTS_PASSWORD_VIEW_URL)
			.param("newPassword", newPw)
			.param("newPasswordConfirm", newPw)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(flash().attributeExists("message"));

		// then
		Member findMember = memberRepo.findByEmail(TEST_EMAIL).orElseThrow(NullPointerException::new);
		assertTrue(passwordEncoder.matches(newPw, findMember.getPassword()));
	}


	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("비밀번호 변경하기 - 입력값 에러")
	@Test
	void updatePassword_error_unMatchedPassword() throws Exception {
		String newPw = "1234567890";
		mockMvc.perform(post(ACCOUNTS_PASSWORD_VIEW_URL)
			.param("newPassword", newPw)
			.param("newPasswordConfirm", newPw+"abc")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PASSWORD_VIEW_NAME))
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("member"));
	}

}