package com.techdot.techdot.modules.member;

import static com.techdot.techdot.infra.Constant.*;
import static com.techdot.techdot.modules.member.AccountsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@MockMvcTest
class AccountsControllerTest extends AbstractContainerBaseTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private final String TEST_NICKNAME = "testNickname";
	private final String REQUEST_URL = "/accounts";

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("계정 프로필 설정 뷰 (메인 뷰)")
	@Test
	void accountsProfileSettingsView_Success() throws Exception {
		mockMvc.perform(get(REQUEST_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_PROFILE_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("계정 프로필 수정하기 폼 요청 성공")
	@Test
	void accountsProfileUpdateForm_Success() throws Exception {
		String bio = "소개를 수정하는 경우";
		mockMvc.perform(post(REQUEST_URL)
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

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("계정 프로필 수정하기 폼 요청 실패 - 소개글 범위가 주어진 범위를 초과한 경우")
	@Test
	void accountsProfileUpdateForm_BioTextLengthIsOutOfRange_Error() throws Exception {
		String bio = "소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.소개를 수정하는 길이가 너무 긴 경우.";
		mockMvc.perform(post(REQUEST_URL)
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

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("프로필 수정하기 폼 요청 실패 - 중복된 닉네임일 경우")
	@Test
	void accountsProfileUpdateForm_NicknameIsDuplicated_Error() throws Exception {
		// given
		Member newMember = memberRepository.save(Member.builder()
			.email("test2@naver.com")
			.password("12345678")
			.nickname("test")
			.emailVerified(true)
			.build());

		mockMvc.perform(post(REQUEST_URL)
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

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("비밀번호 변경 뷰")
	@Test
	void accountsUpdatePasswordView_Success() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("passwordForm"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("비밀번호 변경하기 폼 요청 성공")
	@Test
	void accountsUpdatePasswordForm_Success() throws Exception {
		String newPw = "1234567890";
		mockMvc.perform(post(REQUEST_URL + ACCOUNTS_PASSWORD_VIEW_URL)
			.param("newPassword", newPw)
			.param("newPasswordConfirm", newPw)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts" + ACCOUNTS_PASSWORD_VIEW_URL))
			.andExpect(flash().attributeExists("message"));

		// then
		Member findMember = memberRepository.findByEmail(TEST_EMAIL).orElseThrow(NullPointerException::new);
		assertTrue(passwordEncoder.matches(newPw, findMember.getPassword()));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("비밀번호 변경하기 폼 요청 실패 - 비밀번호와 비밀번호 확인 값이 일치하지 않을 경우")
	@Test
	void accountsUpdatePasswordForm_PasswordAndPasswordConfirmNotMatched_Error() throws Exception {
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

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 관리 뷰 - 관리자만 접속 가능")
	@Test
	void accountsSettingCategoryView_OnlyAdmin_Success() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_CATEGORY_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_CATEGORY_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("계정이 업로드한 게시글 관리 뷰 - 관리자만 접속 가능")
	@Test
	void accountsMyUploadView_OnlyAdmin_Success() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_MY_UPLOAD_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_MY_UPLOAD_VIEW_NAME))
			.andExpect(model().attributeExists("postPage"))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("계정 설정 뷰")
	@Test
	void accountsSettingView_Success() throws Exception {
		mockMvc.perform(get(REQUEST_URL + ACCOUNTS_SETTING_VIEW_URL))
			.andExpect(status().isOk())
			.andExpect(view().name(ACCOUNTS_SETTING_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("계정 회원 탈퇴 요청 성공")
	@Test
	void accountsWithdrawal_Success() throws Exception {
		mockMvc.perform(post(REQUEST_URL + "/withdrawal")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}
}