package com.techdot.techdot.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.auth.WithCurrentUser;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.dto.JoinFormDto;
import com.techdot.techdot.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepo;

	@BeforeEach
	void setUp(){
		JoinFormDto joinFormDto = new JoinFormDto();
		joinFormDto.setNickname("loosie");
		joinFormDto.setEmail("test@naver.com");
		joinFormDto.setPassword("12345678");
		joinFormDto.setTermsCheck(true);
		memberService.save(joinFormDto);
	}

	@AfterEach
	void end(){
		memberRepo.deleteAll();
	}

	@DisplayName("로그인 하기")
	@Test
	void login_success() throws Exception {
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
	void login_failed() throws Exception {
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
	void logout() throws Exception {
		mockMvc.perform(post("/logout")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(unauthenticated());
	}

	@DisplayName("카테고리 별로 뷰 보여주기")
	@Test
	void mainByCategoryView() {
		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			try {
				mockMvc.perform(get("/category/" + categoryName.getViewName()))
					.andExpect(status().isOk())
					.andExpect(view().name(CategoryName.getMainViewName(categoryName.getViewName())))
					.andExpect(unauthenticated());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@WithCurrentUser(value = "test1@naver.com", role="MEMBER")
	@DisplayName("관심 카테고리 뷰 보여주기 - 이메일 인증 받지 않은 경")
	@Test
	void mainMyInterestsView() throws Exception{
		mockMvc.perform(get("/me/interests"))
			.andExpect(status().isOk())
			.andExpect(view().name("main/my-interests"))
			.andExpect(authenticated());
	}

	@DisplayName("검색 뷰 보여주기")
	@Test
	void searchView() throws Exception{
		mockMvc.perform(get("/search")
			.param("keyword", "검색")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("keyword"))
			.andExpect(view().name("search"))
			.andExpect(unauthenticated());
	}

	@DisplayName("에러 뷰 보여주기")
	@Test
	void errorView() throws Exception{
		mockMvc.perform(get("/error/403"))
			.andExpect(status().isOk())
			.andExpect(view().name("error/403"))
			.andExpect(unauthenticated());
	}
}