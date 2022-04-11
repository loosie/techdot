package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;

@TCDataJpaTest
class CategoryRepositoryTest extends AbstractContainerBaseTest {

	@Autowired private CategoryRepository categoryRepository;

	@DisplayName("카테고리 생성하기")
	@Test
	void member_create_success() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();

		// when
		categoryRepository.save(category);

		// then
		assertEquals(category.getTitle(), "JAVA");
	}

	@DisplayName("카테고리 viewName으로 조회하기")
	@Test
	void category_findViewName() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();
		categoryRepository.save(category);

		// when
		Category findCategory = categoryRepository.getByViewName("java");

		// then
		assertEquals(findCategory.getViewName(), "java");
		assertEquals(findCategory.getTitle(), "JAVA");
		assertEquals(findCategory.getName(), "자바");
	}

	@DisplayName("카테고리 존재여부 확인하기 (Name, Title, ViewName)")
	@Test
	void category_isExisted_data() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();

		// when
		categoryRepository.save(category);

		// then
		assertTrue(categoryRepository.existsByName("자바"));
		assertTrue(categoryRepository.existsByTitle("JAVA"));
		assertTrue(categoryRepository.existsByViewName("java"));
	}

	@DisplayName("카테고리에 속한 게시글 조회하기")
	@Test
	void category_findPostsByCategoryId() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();
		categoryRepository.save(category);

		// when, then
		assertEquals(categoryRepository.findPostsByCategoryId(0L).size(), 0);
	}
}