package com.techdot.techdot.modules.like;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.techdot.techdot.modules.post.Category;
import com.techdot.techdot.modules.post.CategoryName;
import com.techdot.techdot.modules.post.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.post.PostRepository;
import com.techdot.techdot.modules.post.PostType;
import com.techdot.techdot.modules.member.MemberRepository;

@DataJpaTest
class LikeRepositoryTest {

	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private Member member;
	private Category category;
	private Post post;

	@BeforeEach
	void setUp(){
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
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
			.uploadDateTime(LocalDateTime.now())
			.writer("naver")
			.manager(member)
			.build();

		memberRepository.save(member);
		categoryRepository.save(category);
		postRepository.save(post);
	}

	@DisplayName("좋아요 생성하기 - 성공")
	@Test
	void like_create_success(){
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
	void like_findByMemberAndPost(){
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


}