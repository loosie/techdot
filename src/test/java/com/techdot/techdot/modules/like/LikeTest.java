package com.techdot.techdot.modules.like;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.post.PostType;

class LikeTest {

	@DisplayName("좋아요 생성 오유 - 입력 값 null인 경우")
	@Test
	void likeCreate_InsertValueIsNull_ExceptionThrown(){
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		Post post = Post.builder()
			.title("title1")
			.content("content.content...")
			.link("http://~~~.com")
			.type(PostType.BLOG)
			.writer("naver")
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();

		// when, then
		assertThrows(IllegalArgumentException.class, () -> Like.builder()
			.member(member).post(null).build());
		assertThrows(IllegalArgumentException.class, () -> Like.builder()
			.member(null).post(post).build());
	}

}