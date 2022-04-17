package com.techdot.techdot.modules.interest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.member.Member;

class InterestTest {

	@DisplayName("관심 생성 실패 - 입력값 null인 경우")
	@Test
	void interestCreate_InsertValueIsNull_ExceptionThrown(){
		// given
		Member member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		Category category =  Category.builder()
			.viewName("java")
			.title("자바")
			.name("Java")
			.build();

		// when, then
		assertThrows(IllegalArgumentException.class, () -> Interest.builder()
			.member(member)
			.category(null)
			.build());

		assertThrows(IllegalArgumentException.class, () -> Interest.builder()
			.member(null)
			.category(category)
			.build());
	}
}