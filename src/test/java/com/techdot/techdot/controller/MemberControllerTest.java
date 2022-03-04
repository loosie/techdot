package com.techdot.techdot.controller;

import static com.techdot.techdot.controller.MemberController.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.auth.WithCurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepo;

	@MockBean
	private JavaMailSender javaMailSender;

	@AfterEach
	void end(){
		memberRepo.deleteAll();
	}

	private final String TEST_EMAIL = "test@naver.com";
	private final String TEST_NICKNAME = "testNickname";

	@DisplayName("회원 가입 화면 뷰 테스트")
	@Test
	void memberJoinView() throws Exception {
		mockMvc.perform(get("/join"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/join"))
			.andExpect(model().attributeExists("joinForm"))
			.andExpect(unauthenticated());
	}

	@DisplayName("회원 가입 테스트 - 정상")
	@Test
	void memberJoin_success() throws Exception {
		mockMvc.perform(post("/join")
			.param("nickname", "testnickname")
			.param("email", "test@naver.com")
			.param("password", "12345678")
			.param("passwordConfirm", "12345678")
			.param("termsCheck", "true")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/check-email"))
			.andExpect(unauthenticated());

		// then
		Optional<Member> member = memberRepo.findByEmail("test@naver.com");
		assertNotNull(member);
		assertThat(!member.get().getPassword().equals("12345678"));
		assertFalse(member.get().getEmailVerified());
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}

	@DisplayName("회원 가입 테스트 - 입력값 오류")
	@Test
	void memberJoin_error_wrongInput() throws Exception {
		mockMvc.perform(post("/join")
			.param("nickname", "loosie")
			.param("email", "email...")
			.param("password", "1234")
			.param("passwordConfirm", "1234")
			.param("termsCheck", "true")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/join"))
			.andExpect(unauthenticated());
	}

	@DisplayName("인증 메일 확인 - 정상")
	@Transactional
	@Test
	void emailConfirm_success() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.emailVerified(false)
			.termsCheck(true)
			.nickname("testNickname")
			.build();
		Member newMember = memberRepo.save(member);
		newMember.generateEmailCheckToken();

		// when, then
		mockMvc.perform(get("/confirm-email")
			.param("token", newMember.getEmailCheckToken())
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(model().attributeDoesNotExist("error"))
			.andExpect(model().attributeExists("nickname"))
			.andExpect(view().name("member/confirm-email"))
			.andExpect(authenticated());

		// then
		assertTrue(newMember.getEmailVerified());
	}

	@DisplayName("인증 메일 확인 - 입력값 오류")
	@Test
	void emailConfirm_error_wrongInput() throws Exception {
		mockMvc.perform(get("/confirm-email")
			.param("token", "testToken")
			.param("email", "test@naver.com"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("error"))
			.andExpect(view().name("member/confirm-email"))
			.andExpect(unauthenticated());
	}

	// 인증 메일 확인 테스트와 동일
	@DisplayName("이메일로 로그인하기 - 정상")
	@Transactional
	@Test
	void emailLogin_sucess() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.nickname("testNickname")
			.emailVerified(false)
			.termsCheck(true)
			.build();
		Member newMember = memberRepo.save(member);
		newMember.generateEmailCheckToken();

		mockMvc.perform(get("/login-by-email")
			.param("token", newMember.getEmailCheckToken())
			.param("email", newMember.getEmail()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/password"))
			.andExpect(authenticated());
	}

	// 인증 메일 확인 테스트와 동일
	@DisplayName("이메일로 로그인하기 - 입력값 오류")
	@Transactional
	@Test
	void emailLogin_error_wrongValue() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.nickname("testNickname")
			.emailVerified(false)
			.termsCheck(true)
			.build();
		Member newMember = memberRepo.save(member);
		newMember.generateEmailCheckToken();

		mockMvc.perform(get("/login-by-email")
			.param("token", "testToken")
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/email-login"))
			.andExpect(unauthenticated());
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
}