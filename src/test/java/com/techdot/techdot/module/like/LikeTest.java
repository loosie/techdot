package com.techdot.techdot.module.like;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.module.category.Category;
import com.techdot.techdot.module.category.CategoryName;
import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.post.Post;
import com.techdot.techdot.module.post.PostType;

class LikeTest {

	@DisplayName("좋아요 생성 실패 - 입력값 오류 member or post null")
	@Test
	void like_create_fail_nullValue(){
		// given
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
			.uploadDateTime(LocalDateTime.now())
			.manager(member)
			.build();

		// when, then
		assertThrows(IllegalArgumentException.class, () -> Like.builder()
			.member(member)
			.post(null)
			.build());

		assertThrows(IllegalArgumentException.class, () -> Like.builder()
			.member(null)
			.post(post)
			.build());
	}

}