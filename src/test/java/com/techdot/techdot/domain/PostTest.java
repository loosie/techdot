package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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

		Category category = Category.builder()
			.name(CategoryName.CS)
			.build();

		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.category(category)
			.writer("naver")
			.manager(member)
			.build();

		assertEquals(post.getTitle(), "title1");
		assertEquals(post.getManager(), member);
		assertEquals(post.getType(), PostType.BLOG);
	}

	@DisplayName("게시글 생성하기 - 입력값 오류 manager, category null")
	@Test
	void post_create_fail_nullValue() {
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.title("title1") // member null
			.content("content.content...")
			.link("http://~~~.com")
			.writer("naver")
			.type(PostType.BLOG)
			.build());
	}

	@DisplayName("게시글 링크 정규식 테스트")
	@Test
	void postLink_regex(){
		String regex = "https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@!:%_\\+.~#?&//=]*)";
		Pattern compile = Pattern.compile(regex);

		List<String> links = List.of("http://www.naver.com", "https://www.google.com/search?q=abcd!&rlz=1C5CHFA_enKR910KR910&oq=abcd!&aqs=chrome..69i57j0i512l9.1903j0j4&sourceid=chrome&ie=UTF-8", "http://bit.ly/", "http://bit.ly/asd1-sq",
			"https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard", "https://loosie.tistory.com/758", "https://velog.io/@youns1121/failed-to-lazily-initialize-a-collection-of-role-could-not-initialize-proxy-no-Session" );

		links.stream().forEach(link -> assertTrue(compile.matcher(link).matches()));
	}
}