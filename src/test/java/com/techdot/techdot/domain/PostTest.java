package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

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

	@Test
	void link_regex(){
		String regex = "https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
		Pattern compile = Pattern.compile(regex);

		System.out.println(compile.matcher("http://www.naver.com").matches());
		System.out.println(compile.matcher("http://naver.com").matches());
		System.out.println(compile.matcher("http://bit.ly/").matches());
		System.out.println(compile.matcher("http://bit.ly/asd1-sq").matches());
		System.out.println(compile.matcher("https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard").matches());
		System.out.println(compile.matcher("https://loosie.tistory.com/758").matches());
		System.out.println(compile.matcher("https://velog.io/@youns1121/failed-to-lazily-initialize-a-collection-of-role-could-not-initialize-proxy-no-Session").matches());

	}
}