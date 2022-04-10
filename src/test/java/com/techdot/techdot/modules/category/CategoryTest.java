package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.regex.Pattern;

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

	@DisplayName("카테고리 viewName 정규식 테스트")
	@Test
	void categoryViewName_regex() {
		String regex = "^[a-z0-9,-]{1,20}$";
		Pattern compile = Pattern.compile(regex);

		List<String> viewNames = List.of("abc", "ab-c", "abc0-2", "abc123");
		viewNames.stream().forEach(viewName -> assertTrue(compile.matcher(viewName).matches()));

		List<String> notViewNames = List.of("Abc", "abc!", "에이비씨", "abc가");
		notViewNames.stream().forEach(viewName -> assertTrue(!compile.matcher(viewName).matches()));
	}
}
