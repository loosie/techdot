package com.techdot.techdot.modules.like;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.post.PostRepository;
import com.techdot.techdot.modules.post.PostType;
import com.techdot.techdot.modules.member.MemberRepository;

@TCDataJpaTest
class LikeRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;

	private Member member;
	private Post post;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.uploadDateTime(LocalDateTime.now())
			.writer("naver")
			.manager(member)
			.build();

		memberRepository.save(member);
		postRepository.save(post);
	}

	@DisplayName("좋아요 생성하기 - 성공")
	@Test
	void like_create_success() {
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		Like saveLike = likeRepository.save(like);

		// then
		assertEquals(saveLike.getPost(), post);
		assertEquals(saveLike.getMember(), member);
	}

	@DisplayName("멤버와 게시글로 좋아요 조회하기")
	@Test
	void like_findByMemberAndPost() {
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		likeRepository.save(like);

		// when
		Like findLike = likeRepository.findByMemberAndPost(member, post).get();

		// then
		assertEquals(findLike.getPost(), post);
		assertEquals(findLike.getMember(), member);
	}

	@DisplayName("멤버 id로 해당 멤버 좋아요 모두 삭제하기")
	@Test
	void likes_allDelete_byMemberId(){
		// given
		Like like = Like.builder()
			.member(member)
			.post(post)
			.build();
		likeRepository.save(like);

		// when
		assertTrue(likeRepository.findByMemberAndPost(member, post).isPresent());
		likeRepository.deleteAllByMemberId(member.getId());

		// then
		assertTrue(likeRepository.findByMemberAndPost(member, post).isEmpty());
	}

}