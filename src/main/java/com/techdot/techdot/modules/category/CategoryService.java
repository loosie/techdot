package com.techdot.techdot.modules.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 생성하기
	 * @param categoryForm
	 */
	public void save(final CategoryFormDto categoryForm) {
		Category newCategory = Category.builder()
			.name(categoryForm.getName())
			.title(categoryForm.getTitle())
			.viewName(categoryForm.getViewName())
			.build();

		categoryRepository.save(newCategory);
	}

	public List<Category> getAll() {
		return categoryRepository.findAll();
	}

	public Category getByViewName(final String viewName) {
		Optional<Category> category = categoryRepository.findByViewName(viewName);
		if (category.isEmpty()) {
			throw new NullPointerException(viewName + " 해당 카테고리는 존재하지 않습니다.");
		}
		return category.get();
	}

	public void update(Long id, CategoryFormDto categoryForm) {
		Category category = getById(id);
		category.update(categoryForm);
	}

	public Category getById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 카테고리는 존재하지 않습니다."));
	}


}
