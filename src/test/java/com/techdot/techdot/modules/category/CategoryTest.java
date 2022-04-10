package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CategoryTest {

	@DisplayName("카테고리 생성 오류 - 입력 값이 null인 경우")
	@Test
	void category_create_fail_nullValue() {
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName(null).title("자바").name("Java").build());
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName("java").title(null).name("Java").build());
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName("java").title("자바").name(null).build());
	}
}
