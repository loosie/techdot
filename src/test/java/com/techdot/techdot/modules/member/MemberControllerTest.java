package com.techdot.techdot.modules.member;

import static com.techdot.techdot.modules.member.MemberController.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.infra.mail.EmailMessageDto;
import com.techdot.techdot.infra.mail.EmailService;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@MockMvcTest
class MemberControllerTest extends AbstractContainerBaseTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private MemberRepository memberRepository;
	@MockBean private EmailService emailService;


	private final String TEST_EMAIL = "test@naver.com";

	@DisplayName("회원 가입 화면 뷰")
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
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(MEMBER_CHECK_EMAIL_VIEW_NAME))
			.andExpect(unauthenticated());

		// then
		Optional<Member> member = memberRepository.findByEmail("test@naver.com");
		assertNotNull(member);
		assertThat(!member.get().getPassword().equals("12345678"));
		assertFalse(member.get().getEmailVerified());
		then(emailService).should().sendEmail(any(EmailMessageDto.class));
	}

	@DisplayName("회원 가입 테스트 - 입력값 오류")
	@Test
	void memberJoin_error_wrongInput() throws Exception {
		mockMvc.perform(post("/join")
			.param("nickname", "loosie")
			.param("email", "email...")
			.param("password", "1234")
			.param("passwordConfirm", "1234")
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
			.nickname("testNickname")
			.build();
		Member newMember = memberRepository.save(member);
		newMember.generateEmailCheckToken();

		// when, then
		mockMvc.perform(get("/confirm-email")
			.param("token", newMember.getEmailCheckToken())
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(model().attributeDoesNotExist("message"))
			.andExpect(model().attributeExists("nickname"))
			.andExpect(view().name("member/confirm-email"))
			.andExpect(authenticated());
		assertTrue(newMember.getEmailVerified());
	}

	@DisplayName("인증 메일 확인 - 입력값 오류")
	@Test
	void emailConfirm_error_wrongInput() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.emailVerified(false)
			.nickname("testNickname")
			.build();
		Member newMember = memberRepository.save(member);
		newMember.generateEmailCheckToken();

		mockMvc.perform(get("/confirm-email")
			.param("token", "testToken")
			.param("email", "test@naver.com"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("message"))
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
			.build();
		Member newMember = memberRepository.save(member);
		newMember.generateEmailCheckToken();

		mockMvc.perform(get("/login-by-email")
			.param("token", newMember.getEmailCheckToken())
			.param("email", newMember.getEmail()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/change-password"))
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
			.build();
		Member newMember = memberRepository.save(member);
		newMember.generateEmailCheckToken();

		mockMvc.perform(get("/login-by-email")
			.param("token", "testToken")
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/email-login"))
			.andExpect(unauthenticated());

	}

	@WithCurrentUser(value = TEST_EMAIL, role="MEMBER")
	@DisplayName("내가 좋아요한 게시글 뷰")
	@Test
	void profileForm() throws Exception {
		mockMvc.perform(get("/me/likes"))
			.andExpect(status().isOk())
			.andExpect(view().name(MEMBER_ME_LIKES_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}


}