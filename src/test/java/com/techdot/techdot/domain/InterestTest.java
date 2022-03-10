package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InterestTest {

	@DisplayName("관심 생성 실패 - 입력값 오류 member or category null")
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