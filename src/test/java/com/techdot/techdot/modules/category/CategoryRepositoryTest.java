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
	void categoryCreate_Success() {
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
	void categoryGetOrFindByViewName_Success() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();
		categoryRepository.save(category);

		// when
		Category getCategory = categoryRepository.getByViewName("java");
		Category findCategory = categoryRepository.findByViewName("java").get();

		// then
		assertEquals(getCategory.getViewName(), "java");
		assertEquals(getCategory.getTitle(), "JAVA");
		assertEquals(getCategory.getName(), "자바");
		assertEquals(findCategory.getViewName(), getCategory.getViewName());
		assertEquals(findCategory.getTitle(), getCategory.getTitle());
		assertEquals(findCategory.getName(), getCategory.getName());
	}

	@DisplayName("카테고리 존재여부 확인하기 (Name, Title, ViewName)")
	@Test
	void categoryExistsByNameOrTitleOrViewName_Success() {
		// given
		Category category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.build();
		categoryRepository.save(category);

		// when, then
		assertTrue(categoryRepository.existsByName("자바"));
		assertTrue(categoryRepository.existsByTitle("JAVA"));
		assertTrue(categoryRepository.existsByViewName("java"));
	}

	@DisplayName("카테고리에 속한 게시글 조회하기")
	@Test
	void categoryFindPostsByCategoryId_Success() {
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