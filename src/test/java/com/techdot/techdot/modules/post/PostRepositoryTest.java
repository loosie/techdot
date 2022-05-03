package com.techdot.techdot.modules.post;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto;

@TCDataJpaTest
class PostRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private Member member;
	private Category category;

	@BeforeEach
	void setUp() {
		//given
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		category = Category.builder()
			.name("자바").title("Java title").viewName("java").displayOrder(1).build();

		// when
		memberRepository.save(member);
		categoryRepository.save(category);
	}

	@DisplayName("게시글 생성하기")
	@Test
	void postCreate_Success() {
		//given
		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.category(category)
			.writer("naver")
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();

		// when
		Post savePost = postRepository.save(post);

		// then
		assertEquals(savePost.getTitle(), "title1");
		assertEquals(savePost.getManager(), member);
		assertEquals(savePost.getType(), PostType.BLOG);
	}

	@DisplayName("게시글 Link로 존재여부 확인하기")
	@Test
	void postExistsByLink_Success() {
		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://testlink.com")
			.type(PostType.BLOG)
			.category(category)
			.uploadDateTime(LocalDateTime.now())
			.writer("naver")
			.manager(member)
			.build();
		postRepository.save(post);

		// when, then
		assertTrue(postRepository.existsByLink("http://testlink.com"));
	}

	@DisplayName("Manager로 게시글 정보 가져오기")
	@Test
	void postGetByManager_Success() {
		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://testlink.com")
			.type(PostType.BLOG)
			.category(category)
			.writer("naver")
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();
		Post savePost = postRepository.save(post);

		// when
		Page<MyUploadPostResponseDto> byManager = postRepository.getByManager(member, Pageable.ofSize(1));

		// then
		MyUploadPostResponseDto result = byManager.getContent().get(0);
		assertEquals(result.getId(), savePost.getId());
		assertEquals(result.getTitle(), "title1");
		assertEquals(result.getWriter(), "naver");
	}

	@DisplayName("게시글 ID로 게시글 삭제하기")
	@Test
	void postDeleteById_Success() {
		// given
		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://testlink.com")
			.type(PostType.BLOG)
			.category(category)
			.writer("naver")
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();
		Post savePost = postRepository.save(post);

		// when
		postRepository.deleteById(savePost.getId());

		// then
		assertTrue(postRepository.findById(savePost.getId()).isEmpty());
	}

}