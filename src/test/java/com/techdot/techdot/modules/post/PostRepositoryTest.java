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
import com.techdot.techdot.modules.category.CategoryName;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;

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
	void setUp(){
		//given
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		category = Category.builder()
			.name(CategoryName.CS)
			.build();

		// when
		memberRepository.save(member);
		categoryRepository.save(category);
	}


	@DisplayName("게시글 생성하기 - 성공")
	@Test
	void post_create_success() {
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

	@DisplayName("게시글 링크 존재여부 확인하기")
	@Test
	void post_isExistedLink() {
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

		// when
		postRepository.save(post);

		// then
		assertTrue(postRepository.existsByLink("http://testlink.com"));
	}

	@DisplayName("게시글 Manager정보로 조회하기")
	@Test
	void post_findByManager(){
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

		// when
		Post savePost = postRepository.save(post);

		// then
		Page<Post> byManager = postRepository.findByManager(member, Pageable.ofSize(1));
		assertEquals(byManager.getContent().get(0).getId(), savePost.getId());

	}

}