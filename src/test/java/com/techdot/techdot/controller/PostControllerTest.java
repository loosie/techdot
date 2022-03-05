package com.techdot.techdot.controller;

import static com.techdot.techdot.controller.AccountsController.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.auth.WithCurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;
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
			.param("beforeLink", "http://google.com/")
			.param("link", "http://google.com")
			.param("writer", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(TEST_EMAIL)
	@DisplayName("게시글 업로드 실패 - 입력값 오류 link")
	@Test
	void uploadNewPost_error_wrongLinkValue() throws Exception {
		mockMvc.perform(post("/new-post")
			.param("title", "title")
			.param("content", "content")
			.param("beforeLink", "http://google.com/")
			.param("link", "//google.com")
			.param("writer", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("post/form"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(TEST_EMAIL)
	@Transactional
	@DisplayName("게시글 수정하기 성공")
	@Test
	void updatePost_success() throws Exception {
		// given
		Member member = memberRepo.findByEmail(TEST_EMAIL).get();
		Post post = Post.builder()
			.title("title")
			.content("content")
			.writer("google")
			.link("http://google.com/")
			.type(PostType.BLOG)
			.manager(member)
			.build();
		Post save = postRepo.save(post);

		// when, then
		mockMvc.perform(post("/post/"+save.getId()+"/edit")
			.param("title", "updateTitle")
			.param("content", "content2222")
			.param("beforeLink", "http://google.com/")
			.param("link", "http://google.com/asdasd")
			.param("writer", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"))
			.andExpect(authenticated());

		// then
		Post changePost = postRepo.findById(save.getId()).get();
		assertEquals("updateTitle", changePost.getTitle());
	}

	@WithCurrentUser(TEST_EMAIL)
	@Transactional
	@DisplayName("게시글 수정하기 실패 - 입력값 오류 link")
	@Test
	void updatePost_fail_notAuth() throws Exception {
		// given
		Member member = memberRepo.findByEmail(TEST_EMAIL).get();
		Post post = Post.builder()
			.title("title")
			.content("content")
			.writer("google")
			.link("http://google.com/")
			.type(PostType.BLOG)
			.manager(member)
			.build();
		Post save = postRepo.save(post);

		// when, then
		mockMvc.perform(post("/post/"+save.getId()+"/edit")
			.param("title", "updateTitle")
			.param("content", "content2222")
			.param("beforeLink", "http://google.com/")
			.param("link", "oogle.com/asdasd")
			.param("writer", "google")
			.param("type", "BLOG")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("member"))
			.andExpect(view().name("post/updateForm"))
			.andExpect(authenticated());
	}
}
