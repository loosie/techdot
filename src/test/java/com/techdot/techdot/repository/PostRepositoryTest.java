package com.techdot.techdot.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;

@DataJpaTest
class PostRepositoryTest {

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
			.termsCheck(true)
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
			.manager(member)
			.build();

		// when
		Post savePost = postRepository.save(post);

		// then
		Page<Post> byManager = postRepository.findByManager(member, Pageable.ofSize(1));
		assertEquals(byManager.getContent().get(0).getId(), savePost.getId());

	}

}