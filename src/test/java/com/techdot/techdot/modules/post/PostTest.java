package com.techdot.techdot.modules.post;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostFormDto;

class PostTest {

	@DisplayName("게시글 생성 오류 - 입력 값이 null인 경우")
	@Test
	void postCreate_InsertValueIsNull_ExceptionThrown() {
		Member member  = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		Category category = Category.builder()
			.viewName("java").title("JAVA").name("자바").displayOrder(1).build();
		String content = "content.content...";
		String title1 = "title1";
		String link = "http://~~~.com";
		String writer = "writer";
		PostType blog = PostType.BLOG;
		LocalDateTime now = LocalDateTime.now();

		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(null).category(category).title(title1).content(content).link(link).writer(writer).type(blog).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(null).content(content).link(link).writer(writer).type(blog).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(title1).content(null).link(link).writer(writer).type(blog).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(title1).content(content).link(null).writer(writer).type(blog).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(title1).content(content).link(link).writer(null).type(blog).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(title1).content(content).link(link).writer(writer).type(null).uploadDateTime(now).build());
		assertThrows(IllegalArgumentException.class, () -> Post.builder()
			.manager(member).category(category).title(title1).content(content).link(link).writer(writer).type(null).uploadDateTime(null).build());
	}

	@DisplayName("게시글 정보 업데이트하기")
	@Test
	void postUpdate_Success(){
		Member member  = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
		Category category = Category.builder()
			.viewName("java").title("JAVA").name("자바").displayOrder(1).build();
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

		PostFormDto postFormDto = new PostFormDto();
		postFormDto.setTitle("changeTitle");
		Category changeCategory = Category.builder()
			.viewName("changeJava").title("JAVA2").name("자바2").displayOrder(1).build();

		// when
		post.update(postFormDto, changeCategory);

		// then
		assertEquals(post.getTitle(), "changeTitle");
		assertEquals(changeCategory.getViewName(), "changeJava");
	}

	@DisplayName("게시글 Link 정규식 테스트")
	@Test
	void postLink_RegexTest(){
		String regex = "https?://(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@!:%_\\+.~#?&//=]*)";
		Pattern compile = Pattern.compile(regex);

		List<String> links = List.of("http://www.naver.com", "https://www.google.com/search?q=abcd!&rlz=1C5CHFA_enKR910KR910&oq=abcd!&aqs=chrome..69i57j0i512l9.1903j0j4&sourceid=chrome&ie=UTF-8", "http://bit.ly/", "http://bit.ly/asd1-sq",
			"https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard", "https://loosie.tistory.com/758", "https://velog.io/@youns1121/failed-to-lazily-initialize-a-collection-of-role-could-not-initialize-proxy-no-Session" );

		links.stream().forEach(link -> assertTrue(compile.matcher(link).matches()));
	}
}