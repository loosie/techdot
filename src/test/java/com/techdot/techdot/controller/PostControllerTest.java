package com.techdot.techdot.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.auth.WithCurrentUser;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PostRepository postRepo;

	@Autowired
	private MemberRepository memberRepo;

	@AfterEach
	void end() {
		postRepo.deleteAll();
		memberRepo.deleteAll();
	}

	private final String TEST_EMAIL = "test@naver.com";

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("게시글 업로드 뷰 테스트")
	@Test
	void newPostView() throws Exception {
		mockMvc.perform(get("/new-post"))
			.andExpect(status().isOk())
			.andExpect(view().name("post/form"))
			.andExpect(model().attributeExists("postForm"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("게시글 업로드 성공")
	@Test
	void uploadNewPost_success() throws Exception {
		mockMvc.perform(post("/new-post")
			.param("title", "title")
			.param("content", "content")
			.param("link", "http://google.com")
			.param("owner", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("게시글 업로드 실패 - 입력값 오류")
	@Test
	void uploadNewPost_error_wrongLinkValue() throws Exception {
		mockMvc.perform(post("/new-post")
			.param("title", "title")
			.param("content", "content")
			.param("link", "//google.com")
			.param("owner", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("post/form"))
			.andExpect(authenticated());
	}
}
