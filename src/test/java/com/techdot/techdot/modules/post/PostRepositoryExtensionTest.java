package com.techdot.techdot.modules.post;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.interest.Interest;
import com.techdot.techdot.modules.like.Like;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.interest.InterestRepository;
import com.techdot.techdot.modules.like.LikeRepository;

@TCDataJpaTest
class PostRepositoryExtensionTest extends AbstractContainerBaseTest {

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
			.name("자바").title("Java title").viewName("java").displayOrder(1).build();
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

	@DisplayName("모든 게시글 Dto 조회하기 - 페이징 적용")
	@Test
	void postFindAllDto_ApplyPagination_Success() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDto(-1L, PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("카테고리 Viewname에 속하는 모든 게시글 Dto 가져오기 - 페이징 적용")
	@Test
	void postFindAllByCategoryName_ApplyPagination_Success() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("회원이 좋아요 누른 모든 게시글 Dto 가져오기 - 페이징 적용")
	@Test
	void postFindAllByLikesMemberId_ApplyPagination_Success() {
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByLikesMemberId(member.getId(),
			PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertTrue(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("회원 관심 카테고리 목록에 해당하는 모든 게시글 Dto 가져오기 - 페이징 적용")
	@Test
	void postFindAllByInterestMemberId_ApplyPagination_Success() {
		//given
		interestRepository.save(Interest.builder().member(member).category(category).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByInterestsMemberId(member.getId(),
			PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertEquals(post.getCategoryDisplayName(), category.getName());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("keyword를 포함하고 있는 모든 게시글 Dto 가져오기 - 페이징 적용, 검색 범위(title, content, writer, category.name, category.title, category.viewName)")
	@Test
	void postFindAllByKeyword_ApplyPagination_Success() {
		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertFalse(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("keyword를 포함하고 있는 모든 게시글 Dto 가져오기 - 위 조건과 동일, 멤버가 존재하는 경우 게시글 좋아요 여부 조회")
	@Test
	void postFindAllByKeyword_ApplyPaginationAndIfMemberExistsThenGetBooleanMemberIsLike_Success() {
		//given
		likeRepository.save(Like.builder().member(member).post(post).build());

		// when
		List<PostQueryResponseDto> result = postRepository.findAllDtoByKeyword(member.getId(), "title",
			PageRequest.of(1, 12));
		PostQueryResponseDto post = result.get(0);

		// then
		assertTrue(result.size() > 0);
		assertTrue(post.getIsMemberLike());
		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getType(), PostType.BLOG);
	}

}