package com.techdot.techdot.module.like;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.module.category.Category;
import com.techdot.techdot.module.category.CategoryName;
import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.post.Post;
import com.techdot.techdot.module.post.PostType;
import com.techdot.techdot.module.member.MemberRepository;
import com.techdot.techdot.module.post.PostRepository;

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
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		Category category = Category.builder()
			.name(CategoryName.CS)
			.build();

		post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.category(category)
			.uploadDateTime(LocalDateTime.now())
			.writer("naver")
			.manager(member)
			.build();
	}

	@BeforeEach
	void setUp() {
		likeService = new LikeService(memberRepository, postRepository, likeRepository);
	}

	@DisplayName("좋아요 생성하기")
	@Test
	void likeAdd() {
		// given
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.empty());

		// when
		likeService.add(1L, 1L);

		// then
		then(memberRepository).should(times(1)).findById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(1)).save(Like.builder().member(member).post(post).build());

	}

	@DisplayName("좋아요 삭제하기")
	@Test
	void likeRemove() {
		// given
		Like like = Like.builder().member(member).post(post).build();
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(postRepository.findById(1L)).willReturn(Optional.of(post));
		given(likeRepository.findByMemberAndPost(member, post)).willReturn(Optional.of(like));

		// when
		likeService.remove(1L, 1L);

		// then
		then(memberRepository).should(times(1)).findById(any());
		then(postRepository).should(times(1)).findById(any());
		then(likeRepository).should(times(1)).findByMemberAndPost(any(), any());
		then(likeRepository).should(times(1)).delete(like);
	}

}