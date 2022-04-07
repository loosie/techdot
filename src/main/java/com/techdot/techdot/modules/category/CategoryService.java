package com.techdot.techdot.modules.category;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 생성하기
	 * @param categoryForm
	 */
	public void save(CategoryFormDto categoryForm) {
		Category newCategory = Category.builder()
			.name(categoryForm.getName())
			.build();

		categoryRepository.save(newCategory);
	}

	public List<Category> getAll() {
		return categoryRepository.findAll();
	}
}
