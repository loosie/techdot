package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;

class CategoryTest {

	@DisplayName("카테고리 생성 오류 - 입력 값이 null인 경우")
	@Test
	void categoryCreate_InsertValueIsNull_ExceptionThrown() {
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName(null).title("자바").name("Java").displayOrder(1).build());
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName("java").title(null).name("Java").displayOrder(1).build());
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName("java").title("자바").name(null).displayOrder(1).build());
		assertThrows(IllegalArgumentException.class,
			() -> Category.builder().viewName("java").title("자바").name("Java").displayOrder(null).build());
	}

	@DisplayName("카테고리 정보 업데이트하기")
	@Test
	void categoryUpdate_Success() {
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();

		Category updateCategory = Category.builder()
			.viewName("java2").title("자바2").name("Java2").displayOrder(1).build();

		// when
		category.update(new CategoryFormDto(updateCategory));

		// then
		assertEquals(category.getViewName(), "java2");
		assertEquals(category.getTitle(), "자바2");
		assertEquals(category.getName(), "Java2");
	}

	@DisplayName("카테고리 viewName 정규식 테스트")
	@Test
	void categoryViewName_RegexTest() {
		String regex = "^[a-z0-9,-]{1,20}$";
		Pattern compile = Pattern.compile(regex);

		List<String> viewNames = List.of("abc", "ab-c", "abc0-2", "abc123");
		viewNames.stream().forEach(viewName -> assertTrue(compile.matcher(viewName).matches()));

		List<String> notViewNames = List.of("Abc", "abc!", "에이비씨", "abc가");
		notViewNames.stream().forEach(viewName -> assertTrue(!compile.matcher(viewName).matches()));
	}
}
