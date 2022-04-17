package com.techdot.techdot.modules.main;

import static com.techdot.techdot.infra.Constant.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.Constant;
import com.techdot.techdot.modules.member.dto.JoinFormDto;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;
import com.techdot.techdot.modules.member.MemberService;

@MockMvcTest
class MainControllerTest extends AbstractContainerBaseTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private MemberService memberService;

	@BeforeEach
	void setUp(){
		JoinFormDto joinFormDto = new JoinFormDto();
		joinFormDto.setNickname("loosie");
		joinFormDto.setEmail("test@naver.com");
		joinFormDto.setPassword("12345678");
		memberService.save(joinFormDto);
	}

	@DisplayName("로그인 성공")
	@Test
	void userLogin_Success() throws Exception {
		mockMvc.perform(post("/login")
			.param("username", "test@naver.com")
			.param("password", "12345678")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated().withUsername("test@naver.com"));
	}

	@DisplayName("로그인 실패 - 가입되지 않은 이메일")
	@Test
	void userLogin_NotExistedEmail_Fail() throws Exception {
		mockMvc.perform(post("/login")
			.param("username", "1111@naver.com")
			.param("password", "12345678")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?error"))
			.andExpect(unauthenticated());
	}

	@DisplayName("로그아웃")
	@Test
	void logout_Success() throws Exception {
		mockMvc.perform(post("/logout")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(unauthenticated());
	}


	@WithCurrentUser(value = TEST_EMAIL, role= MEMBER)
	@DisplayName("관심 카테고리 뷰 - 이메일 인증 받은 경우")
	@Test
	void mainMyInterestsView_MemberIsAuthenticated_Success() throws Exception {
		mockMvc.perform(get("/me/interests"))
			.andExpect(status().isOk())
			.andExpect(view().name("main/my-interests-view"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role= USER)
	@DisplayName("관심 카테고리 뷰 - 이메일 인증 받지 않은 경우")
	@Test
	void mainMyInterestsView_MemberIsUnAuthenticated_Success() throws Exception{
		mockMvc.perform(get("/me/interests"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/check-email"))
			.andExpect(authenticated());
	}

	@DisplayName("검색 뷰")
	@Test
	void searchView_Success() throws Exception{
		mockMvc.perform(get("/search")
			.param("keyword", "검색")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("keyword"))
			.andExpect(model().attributeExists("categoryList"))
			.andExpect(view().name("search"))
			.andExpect(unauthenticated());
	}

	@DisplayName("에러 뷰")
	@Test
	void errorView_Success() throws Exception{
		mockMvc.perform(get("/error/403"))
			.andExpect(status().isOk())
			.andExpect(view().name("error/403"))
			.andExpect(unauthenticated());
	}
}