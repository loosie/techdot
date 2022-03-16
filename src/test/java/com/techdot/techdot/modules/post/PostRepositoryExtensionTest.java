package com.techdot.techdot.modules.post;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import com.techdot.techdot.modules.interest.Interest;
import com.techdot.techdot.modules.like.Like;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.interest.InterestRepository;
import com.techdot.techdot.modules.like.LikeRepository;

@DataJpaTest
class PostRepositoryExtensionTest {

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private InterestRepository interestRepository;

	private Member member;
	private Category category;
	private Post post;

	@BeforeEach
	void setUp() {
		//given
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(true)
			.build();

		category = Category.builder()
			.name(CategoryName.CS)
			.build();
		post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.uploadDateTime(LocalDateTime.now())
			.category(category)
			.writer("naver")
			.manager(member)
			.build();

		// when
		memberRepository.save(member);
		categoryRepository.save(category);
		postRepository.save(post);
	}

	@DisplayName("모든 게시글 가져오기")
	@Test
	void findAllDto() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDto(-1L, PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("카테고리별로 게시글 가져오기")
	@Test
	void findAllDto_byCategoryName() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("멤버가 좋아요한 게시글 가져오기")
	@Test
	void findAllDto_byLikesMemberId() {
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByLikesMemberId(member.getId(), PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertTrue(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("멤버가 좋아요한 게시글 가져오기")
	@Test
	void findAllDto_byInterestsMemberId() {
		//given
		interestRepository.save(Interest.builder().member(member).category(category).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByInterestsMemberId(member.getId(), PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertEquals(post.getCategoryName(), category.getName().toString());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("keyword로 게시글(title, content, writer) 조회하기 - member가 존재하는 경우")
	@Test
	void findAllDto_byKeyword_NullMember() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("keyword로 게시글(title, content, writer) 검색하기 - member가 존재하지 않는 경우")
	@Test
	void findAllDto_byKeyword_withMember() {
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(member.getId(), "title", PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertTrue(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

}