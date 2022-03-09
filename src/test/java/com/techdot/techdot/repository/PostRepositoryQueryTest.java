package com.techdot.techdot.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;
import com.techdot.techdot.dto.PostQueryDto;

@SpringBootTest
class PostRepositoryQueryTest {

	@Autowired
	private PostRepositoryQueryImpl postRepositoryQuery;

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private LikeRepository likeRepository;

	private Member member;
	private Category category;
	private Post post;

	@BeforeEach
	void setUp() {
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
		post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.category(category)
			.writer("naver")
			.manager(member)
			.build();

		// when
		memberRepository.save(member);
		categoryRepository.save(category);
		postRepository.save(post);
	}

	@DisplayName("카테고리 별로 게시글 조회하기")
	@Test
	void find_post_withCategory() {
		// when
		List<PostQueryDto> result = postRepositoryQuery.findWithCategory("All", PageRequest.of(1, 1));
		PostQueryDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("멤버가 좋아요한 게시글 번호(Id) 조회하기")
	@Test
	void find_postId_withLikesAndCategoryByMember() {
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<Long> result = postRepositoryQuery.findIdWithLikesAndCategoryByMember(member.getId(), "All");

		// then
		assertTrue(result.size() > 0);
		assertEquals(result.get(0), post.getId());
	}

	@Test
	void find_post_withLikesByMember(){
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<PostQueryDto> result = postRepositoryQuery.findWithCategoryAndLikesByMember(member.getId(),
			PageRequest.of(1, 1));

		// then
		assertTrue(result.size() > 0);
	}
}