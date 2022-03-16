package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@DisplayName("카테고리 생성하기")
	@Test
	void member_create_success() {
		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			try {
				Category category = Category.builder()
					.name(categoryName)
					.build();
				Category saveCategory = categoryRepository.save(category);
				assertEquals(saveCategory.getName(), category.getName());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}