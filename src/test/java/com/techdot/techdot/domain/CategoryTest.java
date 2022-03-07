package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {

	@DisplayName("카테고리 생성하기")
	@Test
	void member_create_success() {
		Category category = Category.builder()
			.name(CategoryName.CS)
			.build();

		assertEquals(category.getName(), CategoryName.CS);
	}
}