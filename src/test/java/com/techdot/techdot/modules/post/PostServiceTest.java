package com.techdot.techdot.modules.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.image.PostS3Service;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private PostS3Service s3Service;

	@BeforeEach
	void setUp() {
		postService = new PostService(postRepository, memberRepository, categoryRepository, s3Service);
	}

	@DisplayName("게시글 저장하기 성공")
	@Test
	void postSave_Success(){
		// given
		Member member  = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		Category category = Category.builder().viewName("java").title("JAVA").name("자바").build();
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

		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(categoryRepository.findByViewName("java")).willReturn(Optional.of(category));
		given(postRepository.save(post)).willReturn(post);

		// when
		Post save = postService.save(new PostFormDto(post), 1L);

		// then
		then(memberRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findByViewName(any());
		then(postRepository).should(times(1)).save(any());
		assertEquals(save.getTitle(), "title1");
	}

	@DisplayName("게시글 업데이트하기 성공")
	@Test
	void postUpdate_Success(){
		// given
		Member member  = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		Category category = Category.builder().viewName("java").title("JAVA").name("자바").build();
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

		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(categoryRepository.findByViewName("java")).willReturn(Optional.of(category));
		given(postRepository.save(post)).willReturn(post);

		PostFormDto postFormDto = new PostFormDto();
		postFormDto.setTitle("changeTitle");
		postFormDto.setCategoryName("java");

		// when
		postService.update(1L, postFormDto);

		// then
		then(postRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findByViewName(any());
		then(postRepository).should(times(1)).save(any());
		assertEquals(post.getTitle(), "changeTitle");
	}

	@DisplayName("게시글 삭제하기 성공")
	@Test
	void postRemoveById_Success(){
		// given
		Member member  = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		Category category = Category.builder().viewName("java").title("JAVA").name("자바").build();
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
		given(postRepository.getById(1L)).willReturn(post);

		// when
		postService.remove(1L);

		// then
		then(postRepository).should(times(1)).deleteById(1L);
	}


	/**
	 ****************************************** 쿼리 ********************************************
	 */

	@DisplayName("카테고리에 해당하는 게시글 가져오기")
	@Test
	void getPostsByCategoryName_Success() {
		// given
		List<PostQueryResponseDto> allPosts = List.of(
			new PostQueryResponseDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), "자바", false)
		);
		given(postRepository.findAllDtoByCategoryViewName(-1L, "java", PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryResponseDto> result = postService.getPostsByCategoryNameIfMemberWithMemberLikes(null, "java",
			PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByCategoryViewName(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertFalse(result.get(0).getIsMemberLike());
	}

	@DisplayName("카테고리에 해당하는 게시글 가져오기 - 멤버가 존재하는 경우 게시글 좋아요 여부 조회")
	@Test
	void getPostsByCategoryName_IfMemberExistsThenGetBooleanMemberIsLike_Success() {
		// given
		Member member = Member.builder()
			.id(1L)
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		List<PostQueryResponseDto> allPosts = List.of(
			new PostQueryResponseDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), "자바", true)
		);
		given(postRepository.findAllDtoByCategoryViewName(member.getId(), "java", PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryResponseDto> result = postService.getPostsByCategoryNameIfMemberWithMemberLikes(member, "java",
			PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByCategoryViewName(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("멤버가 좋아요 누른 게시글 가져오기")
	@Test
	void getPostsByLikesMemberId_Success() {
		// given
		List<PostQueryResponseDto> allPosts = List.of(
			new PostQueryResponseDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), "자바", true)
		);
		given(postRepository.findAllDtoByLikesMemberId(1L, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryResponseDto> result = postService.getPostsByLikesMemberId(1L, PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByLikesMemberId(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("멤버 ID로 해당 멤버 관심 카테고리 목록에 속한 게시글 가져오기")
	@Test
	void getPostsByInterestsMemberId_Success() {
		// given
		List<PostQueryResponseDto> allPosts = List.of(
			new PostQueryResponseDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), "자바", true)
		);
		given(postRepository.findAllDtoByInterestsMemberId(1L, PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryResponseDto> result = postService.getPostsByInterestsMemberId(1L, PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByInterestsMemberId(any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

	@DisplayName("keyword에 포함하는 게시글 검색하기")
	@Test
	void getPostsByKeyword_Success() {
		// given
		List<PostQueryResponseDto> allPosts = List.of(
			new PostQueryResponseDto(1L, "title", "content", "http://link.com", "writer", PostType.BLOG,
				"", LocalDateTime.now(), "java", true)
		);
		given(postRepository.findAllDtoByKeyword(-1L, "title", PageRequest.of(1, 1)))
			.willReturn(allPosts);

		// when
		List<PostQueryResponseDto> result = postService.getPostsByKeyword(null, "title", PageRequest.of(1, 1));

		// then
		then(postRepository).should(times(1)).findAllDtoByKeyword(any(), any(), any());
		assertEquals(result.get(0).getPostId(), 1L);
		assertEquals(result.get(0).getContent(), "content");
		assertTrue(result.get(0).getIsMemberLike());
	}

}