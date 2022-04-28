package com.techdot.techdot.modules.like;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.jdi.request.DuplicateRequestException;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.post.PostRepository;
import com.techdot.techdot.modules.post.PostType;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

	private LikeService likeService;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private LikeRepository likeRepository;

	private static Member member;
	private static Post post;

	@BeforeAll
	static void init() {
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://testurl.com")
			.type(PostType.BLOG)
			.uploadDateTime(LocalDateTime.now())
			.writer("writer")
			.manager(member)
			.build();
	}

	@BeforeEach
	void setUp() {
		likeService = new LikeService(memberRepository, postRepository, likeRepository);
	}

	@DisplayName("좋아요 정보 저장하기")
	@Test
	void likeAdd_Success() {
		// given
		given(memberRepository.getById(1L)).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.empty());

		// when
		likeService.add(1L, 1L);

		// then
		then(memberRepository).should(times(1)).getById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(1)).save(Like.builder().member(member).post(post).build());
	}

	@DisplayName("좋아요 정보 저장하기 실패 - post 정보가 null인 경우")
	@Test
	void likeAdd_IfPostIsNull_ExceptionThrown() {
		// given
		given(memberRepository.getById(1L)).willReturn(member);
		given(postRepository.findById(1L)).willThrow(NullPointerException.class);

		// when, then
		Assertions.assertThrows(NullPointerException.class, () -> likeService.add(1L, 1L));
		then(memberRepository).should(times(1)).getById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(0)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(0)).save(Like.builder().member(member).post(post).build());
	}

	@DisplayName("좋아요 정보 저장하기 실패 - 이미 좋아요 누른 게시글일 경우")
	@Test
	void likeAdd_IfMemberAlreadyLikedAPost_ExceptionThrown() {
		// given
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		given(memberRepository.getById(1L)).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.of(like));

		// when, then
		Assertions.assertThrows(DuplicateRequestException.class, () -> likeService.add(1L, 1L));
		then(memberRepository).should(times(1)).getById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(0)).save(Like.builder().member(member).post(post).build());
	}

	@DisplayName("좋아요 정보 삭제하기")
	@Test
	void likeRemove_Success() {
		// given
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		given(memberRepository.getById(1L)).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.of(like));

		// when
		likeService.remove(1L, 1L);

		// then
		then(memberRepository).should(times(1)).getById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(1)).delete(like);
	}

	@DisplayName("좋아요 정보 삭제하기 실패 - 등록된 좋아요 정보가 없을 경우")
	@Test
	void likeRemove_IfLikeNotExist_ExceptionThrown() {
		// given
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		given(memberRepository.getById(1L)).willReturn(member);
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.empty());

		// when
		Assertions.assertThrows(NullPointerException.class, () -> likeService.remove(1L, 1L));
		then(memberRepository).should(times(1)).getById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(0)).delete(like);

	}

}