package com.techdot.techdot.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {

	@DisplayName("카테고리 생성하기")
	@Test
	void member_create_success() {
		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			try {
				Category category = Category.builder()
					.name(categoryName)
					.build();
				assertEquals(category.getName(), categoryName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}