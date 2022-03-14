package com.techdot.techdot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.PostType;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		postService = new PostService(postRepository, memberRepository, categoryRepository);
	}

	@DisplayName("카테고리별로 게시글 가져오기 - 멤버가 존재하지 않는 경우")
	@Test
	void getPosts_byCategory() {
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, false)
		);
		given(postRepository.findAllDtoByCategoryName(-1L, CategoryName.CS, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByCategoryNameIfMemberWithMemberLikes(null, "CS",
			PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByCategoryName(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertFalse(result.get(0).getIsMemberLike());
	}

	@DisplayName("카테고리별로 게시글 가져오기 - 멤버가 존재하는 경우")
	@Test
	void getPosts_byCategoryName() {
		// given
		Member member = Member.builder()
			.id(1L)
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, true)
		);
		given(postRepository.findAllDtoByCategoryName(member.getId(), CategoryName.CS, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByCategoryNameIfMemberWithMemberLikes(member, "CS",
			PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByCategoryName(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("멤버가 좋아요 누른 게시글 가져오기")
	@Test
	void getPosts_byLikesMemberId() {
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, true)
		);
		given(postRepository.findAllDtoByLikesMemberId(1L, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByLikesMemberId(1L, PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByLikesMemberId(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("멤버의 관심 카테고리 게시글 가져오기")
	@Test
	void getPosts_byInterestsMemberId() {
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, true)
		);
		given(postRepository.findAllDtoByInterestsMemberId(1L, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByInterestsMemberId(1L, PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByInterestsMemberId(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("keyword로 게시글 검색하기")
	@Test
	void getPosts_byKeyword() {
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, true)
		);
		given(postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByKeyword(null, "title", PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByKeyword(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

}