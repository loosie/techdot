package com.techdot.techdot.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.domain.MemberRepo;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepo memberRepo;

	@MockBean
	private JavaMailSender javaMailSender;

	@DisplayName("회원 가입 화면 뷰 테스트")
	@Test
	void memberJoinForm() throws Exception {
		mockMvc.perform(get("/join"))
			.andExpect(status().isOk())
			.andExpect(view().name("member/join"))
			.andExpect(model().attributeExists("joinForm"));
	}

	@DisplayName("회원 가입 테스트 - 정상")
	@Test
	void memberJoin_success() throws Exception {
		// given when
		mockMvc.perform(post("/join")
			.param("nickname", "loosie")
			.param("email", "test@naver.com")
			.param("password", "12345678")
			.param("passwordConfirm", "12345678")
			.param("termsCheck", "true")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/"));

		// then
		assertThat(memberRepo.existsByEmail("test@naver.com"));
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
			.andExpect(view().name("member/join"));
	}


}