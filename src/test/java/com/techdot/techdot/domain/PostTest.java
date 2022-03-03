package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostTest {

	@DisplayName("게시글 생성하기 - 성공")
	@Test
	void post_create_success() {
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.member(member)
			.build();

		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getMember(), member);
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("게시글 생성하기 - 입력값 오류 member null")
	@Test
	void post_create_fail_nullValue() {
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.title("title1") // member null
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.build());
	}
}