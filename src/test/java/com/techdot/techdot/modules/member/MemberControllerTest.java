package com.techdot.techdot.modules.member;

import static com.techdot.techdot.infra.Constant.*;
import static com.techdot.techdot.infra.util.TokenGenerator.*;
import static com.techdot.techdot.modules.member.MemberController.*;
import static com.techdot.techdot.modules.member.dao.AuthDao.TokenType.*;
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
import com.techdot.techdot.modules.member.dao.AuthDao;

@MockMvcTest
class MemberControllerTest extends AbstractContainerBaseTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AuthDao authDao;
	@MockBean
	private EmailService emailService;

	private final String TEST_EMAIL = "test@naver.com";

	@DisplayName("회원 가입 뷰")
	@Test
	void memberJoinView_Success() throws Exception {
		mockMvc.perform(get("/join"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/join"))
			.andExpect(model().attributeExists("joinForm"))
			.andExpect(unauthenticated());
	}

	@DisplayName("회원 가입 폼 요청 성공")
	@Test
	void memberJoinForm_Success() throws Exception {
		mockMvc.perform(post("/join")
			.param("nickname", "testnickname")
			.param("email", "test@naver.com")
			.param("password", "12345678")
			.param("passwordConfirm", "12345678")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name(CHECK_EMAIL_VIEW_NAME))
			.andExpect(unauthenticated());

		// then
		Optional<Member> member = memberRepository.findByEmail("test@naver.com");
		assertNotNull(member);
		assertThat(!member.get().getPassword().equals("12345678"));
		assertFalse(member.get().getEmailVerified());
		then(emailService).should().sendEmail(any(EmailMessageDto.class));
	}

	@DisplayName("회원 가입 폼 요청 실패 - Email 타입 오류")
	@Test
	void memberJoinFrom_EmailIsNotValidType_Error() throws Exception {
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

	@DisplayName("이메일 인증 확인 링크 요청 성공")
	@Transactional
	@Test
	void memberConfirmEmailLink_Success() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.emailVerified(false)
			.nickname("testNickname")
			.build();
		Member newMember = memberRepository.save(member);
		newMember.countEmailSendTime();
		String token = authDao.saveAndGetAuthToken(newMember.getId(), generateToken(), EMAIL);

		// when, then
		mockMvc.perform(get("/confirm-email")
			.param("token", token)
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(model().attributeDoesNotExist("message"))
			.andExpect(model().attributeExists("nickname"))
			.andExpect(view().name("member/confirm-email"))
			.andExpect(authenticated());
		assertTrue(newMember.getEmailVerified());
	}

	@DisplayName("이메일 인증 확인 링크 요청 실패 - 올바르지 않은 토큰값 오류")
	@Test
	void memberConfirmEmailLink_TokenIsWrongValue_Error() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.emailVerified(false)
			.nickname("testNickname")
			.build();
		Member newMember = memberRepository.save(member);
		newMember.countEmailSendTime();

		mockMvc.perform(get("/confirm-email")
			.param("token", "testToken")
			.param("email", "test@naver.com"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("message"))
			.andExpect(view().name("member/confirm-email"))
			.andExpect(unauthenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = USER)
	@DisplayName("이메일 인증 완료 뷰")
	@Test
	void memberCheckEmailView_Success() throws Exception {
		// given
		mockMvc.perform(get("/check-email")
			.param("email", TEST_EMAIL))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("email"))
			.andExpect(view().name("member/check-email"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = MEMBER)
	@DisplayName("이메일 인증 완료 뷰 - 이미 인증된 회원일 경우")
	@Test
	void memberCheckEmailView_AlreadyAuthenticatedMember_RedirectMainPage() throws Exception {
		mockMvc.perform(get("/check-email")
			.param("email", TEST_EMAIL))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated());
	}

	@DisplayName("이메일로 로그인하기 뷰")
	@Test
	void memberEmailLoginView_Success() throws Exception {
		mockMvc.perform(get("/email-login"))
			.andExpect(view().name("member/email-login"))
			.andExpect(unauthenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = MEMBER)
	@DisplayName("이메일로 로그인하기 링크 보내기 성공")
	@Test
	void memberSendEmailLoginLink_Success() throws Exception {
		mockMvc.perform(post("/email-login")
			.param("email", TEST_EMAIL)
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"))
			.andExpect(redirectedUrl("/email-login"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = MEMBER)
	@DisplayName("이메일로 로그인하기 링크 보내기 실패 - 회원에게 주어진 이메일 요청 횟수가 임계치를 초과했을 경우")
	@Test
	void memberSendEmailLoginLink_UserEmailSendTimeExceedThreshold_Error() throws Exception {
		Member member = memberRepository.findByEmail(TEST_EMAIL).get();
		while(member.canSendConfirmEmail()){
		}

		mockMvc.perform(post("/email-login")
			.param("email", TEST_EMAIL)
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("error"))
			.andExpect(view().name("member/email-login"));
	}

	// 인증 메일 확인 테스트와 동일
	@DisplayName("회원 이메일로 로그인하기 성공")
	@Test
	void memberLoginByEmail_Success() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.nickname("testNickname")
			.emailVerified(false)
			.build();
		Member newMember = memberRepository.save(member);
		newMember.countEmailSendTime();
		String token = authDao.saveAndGetAuthToken(newMember.getId(), generateToken(), LOGIN);

		mockMvc.perform(get("/login-by-email")
			.param("token", token)
			.param("email", newMember.getEmail()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/change-password"))
			.andExpect(authenticated());
	}

	// 인증 메일 확인 테스트와 동일
	@DisplayName("회원 이메일로 로그인하기 실패 - 올바르지 않은 토큰값 오류")
	@Test
	void memberLoginByEmail_TokenIsWrongValue_Error() throws Exception {
		// given
		Member member = Member.builder()
			.email("test@naver.com")
			.password("12345678")
			.nickname("testNickname")
			.emailVerified(false)
			.build();
		Member newMember = memberRepository.save(member);
		newMember.countEmailSendTime();

		mockMvc.perform(get("/login-by-email")
			.param("token", "testToken")
			.param("email", newMember.getEmail()))
			.andExpect(status().isOk())
			.andExpect(view().name("member/email-login"))
			.andExpect(unauthenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = MEMBER)
	@DisplayName("내가 좋아요한 게시글 뷰")
	@Test
	void memberMyLikesView_Success() throws Exception {
		mockMvc.perform(get("/me/likes"))
			.andExpect(status().isOk())
			.andExpect(view().name(ME_LIKES_VIEW_NAME))
			.andExpect(model().attributeExists("member"));
	}

}