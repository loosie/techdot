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

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;
import com.techdot.techdot.repository.PostRepositoryQueryImpl;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	private PostService postService;
	@Mock
	private InterestService interestService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private PostRepositoryQueryImpl postRepositoryQuery;

	@BeforeEach
	void setUp() {
		postService = new PostService(interestService, postRepository, memberRepository, categoryRepository, postRepositoryQuery);
	}

	@DisplayName("카테고리별로 게시글 가져오기 - 멤버가 null인 경우")
	@Test
	void post_getByCategory() {
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), CategoryName.CS, false)
		);
		given(postRepositoryQuery.findQueryDtoByCategoryName("CS", PageRequest.of(1,1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByCategory_andIfMember_memberLikes(null, "CS",
			PageRequest.of(1, 1));

		// then
		then(postRepositoryQuery).should(times(1)).findQueryDtoByCategoryName(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
	}

	@DisplayName("카테고리 별로 멤버가 좋아요 누른 게시글 정보 가져오기")
	@Test
	void post_getByCategoryAndMemberLikes() {
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
				"",  LocalDateTime.now(), CategoryName.CS, true)
		);
		given(postRepositoryQuery.findQueryDtoWithIsMemberLikeByCategoryName(member.getId(), "CS", PageRequest.of(1,1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByCategory_andIfMember_memberLikes(member, "CS",
			PageRequest.of(1, 1));

		// then
		then(postRepositoryQuery).should(times(1)).findQueryDtoWithIsMemberLikeByCategoryName(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("멤버가 좋아요 누른 게시글 가져오기")
	@Test
	void test(){
		// given
		List<PostQueryDto> allPosts = List.of(
			new PostQueryDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"",  LocalDateTime.now(), CategoryName.CS, false)
		);
		given(postRepositoryQuery.findQueryDtoByLikesMemberId(1L, PageRequest.of(1,1)))
			.willReturn(allPosts);

		// when
		List<PostQueryDto> result = postService.getPostsByMemberLikes(1L, PageRequest.of(1, 1));

		// then
		then(postRepositoryQuery).should(times(1)).findQueryDtoByLikesMemberId(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}
}