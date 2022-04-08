package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;

@TCDataJpaTest
class CategoryRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@DisplayName("카테고리 생성하기")
	@Test
	void member_create_success() {
		Category category = categoryRepository.save(
			Category.builder().viewName("java").title("JAVA").name("자바").build());
		assertEquals(category.getTitle(), "JAVA");
	}
}