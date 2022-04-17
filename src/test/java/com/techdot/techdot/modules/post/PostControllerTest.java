package com.techdot.techdot.modules.post;

import static com.techdot.techdot.infra.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@MockMvcTest
class PostControllerTest extends AbstractContainerBaseTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		categoryRepository.save(Category.builder()
			.name("자바")
			.title("Java title")
			.viewName("java")
			.build());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("게시글 업로드 뷰 테스트")
	@Test
	void postCreateView_Success() throws Exception {
		mockMvc.perform(get("/new-post"))
			.andExpect(status().isOk())
			.andExpect(view().name("post/form"))
			.andExpect(model().attributeExists("postForm"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("게시글 업로드 성공")
	@Test
	void postCreate_Success() throws Exception {
		mockMvc.perform(post("/new-post")
			.param("title", "title")
			.param("content", "content")
			.param("curLink", "http://google.com/")
			.param("link", "http://google.com")
			.param("writer", "google")
			.param("categoryName", "java")
			.param("type", "BLOG")
			.param("uploadDateTime", LocalDateTime.now().toString())
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("게시글 업로드 실패 - link 입력값 오류")
	@Test
	void postCreate_LinkIsNotValidType_Fail() throws Exception {
		mockMvc.perform(post("/new-post")
			.param("title", "title")
			.param("content", "content")
			.param("curLink", "http://google.com/")
			.param("link", "//google.com")
			.param("writer", "google")
			.param("categoryName", "java")
			.param("type", "BLOG")
			.param("uploadDateTime", LocalDateTime.now().toString())
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("post/form"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@Transactional
	@DisplayName("게시글 수정하기 성공")
	@Test
	void updatePost_Success() throws Exception {
		// given
		Member member = memberRepository.findByEmail(TEST_EMAIL).get();
		Category category = categoryRepository.getByViewName("java");
		Post post = Post.builder()
			.title("title")
			.content("content")
			.writer("google")
			.link("http://google.com/")
			.type(PostType.BLOG)
			.uploadDateTime(LocalDateTime.now())
			.category(category)
			.manager(member)
			.build();
		Post save = postRepository.save(post);

		// when, then
		mockMvc.perform(post("/post/" + save.getId() + "/edit")
			.param("title", "updateTitle")
			.param("content", "content2222")
			.param("curLink", "http://google.com/")
			.param("link", "http://google.com/asdasd")
			.param("writer", "google")
			.param("categoryName", "java")
			.param("type", "BLOG")
			.param("uploadDateTime", LocalDateTime.now().toString())
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/post/" + save.getId() + "/edit"))
			.andExpect(flash().attributeExists("message"))
			.andExpect(authenticated());

		// then
		Post changePost = postRepository.findById(save.getId()).get();
		assertEquals("updateTitle", changePost.getTitle());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@Transactional
	@DisplayName("게시글 수정하기 실패 - link 입력값 오류")
	@Test
	void updatePost_LinkIsNotValidType_Fail() throws Exception {
		// given
		Member member = memberRepository.findByEmail(TEST_EMAIL).get();
		Category category = categoryRepository.getByViewName("java");
		Post post = Post.builder()
			.title("title")
			.content("content")
			.writer("google")
			.link("http://google.com/")
			.type(PostType.BLOG)
			.category(category)
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();
		Post save = postRepository.save(post);

		// when, then
		mockMvc.perform(post("/post/" + save.getId() + "/edit")
			.param("title", "updateTitle")
			.param("content", "content2222")
			.param("curLink", "http://google.com/")
			.param("link", "oogle.com/asdasd")
			.param("writer", "google")
			.param("categoryName", "java")
			.param("type", "BLOG")
			.param("uploadDateTime", LocalDateTime.now().toString())
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("member"))
			.andExpect(view().name("post/updateForm"))
			.andExpect(authenticated());
	}


	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@Transactional
	@DisplayName("게시글 삭제하기 성공")
	@Test
	void removePost_Success() throws Exception {
		// given
		Member member = memberRepository.findByEmail(TEST_EMAIL).get();
		Post post = Post.builder()
			.title("title")
			.content("content")
			.writer("google")
			.link("http://google.com/")
			.type(PostType.BLOG)
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();
		Post save = postRepository.save(post);

		// when, then
		mockMvc.perform(post("/post/" + save.getId() + "/remove")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/my-upload"))
			.andExpect(authenticated());

		// then
		assertTrue(postRepository.findById(save.getId()).isEmpty());
	}

}
