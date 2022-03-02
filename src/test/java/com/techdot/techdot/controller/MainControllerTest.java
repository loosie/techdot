package com.techdot.techdot.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.domain.MemberRepo;
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
	private MemberRepo memberRepo;

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

}