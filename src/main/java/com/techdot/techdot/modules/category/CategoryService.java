package com.techdot.techdot.modules.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.main.CategoryCanNotDeleteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * 카테고리 생성하기
	 */
	public void save(final CategoryFormDto categoryForm) {
		Category newCategory = Category.builder()
			.name(categoryForm.getName())
			.title(categoryForm.getTitle())
			.viewName(categoryForm.getViewName())
			.build();

		categoryRepository.save(newCategory);
	}


	/**
	 * 모든 카테고리 가져오기
	 */
	public List<Category> getAll() {
		return categoryRepository.findAll();
	}

	/**
	 * viewName으로 카테고리 가져오기
	 */
	public Category getByViewName(final String viewName) {
		return categoryRepository.findByViewName(viewName)
			.orElseThrow(() -> new NullPointerException(viewName + " 해당 카테고리는 존재하지 않습니다."));
	}

	/**
	 * id로 카테고리 가져오기
	 */
	public Category getById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new NullPointerException("해당 카테고리는 존재하지 않습니다."));
	}

	/**
	 * 카테고리 업데이트 하기
	 */
	public void update(Long id, CategoryFormDto categoryForm) {
		Category category = getById(id);
		category.update(categoryForm);
	}

	/**
	 * 카테고리 삭제하기
	 * - 게시글이 존재하는 카테고리는 삭제 불가능
	 */
	public void remove(Long id) {
		if(categoryRepository.findPostsByCategoryId(id).size()>0) {
			throw new CategoryCanNotDeleteException("게시글이 존재하는 카테고리는 삭제할 수 없습니다.");
		}

		categoryRepository.deleteById(id);
		log.info(id +"번 카테고리가 정상적으로 삭제되었습니다.");
	}
}
