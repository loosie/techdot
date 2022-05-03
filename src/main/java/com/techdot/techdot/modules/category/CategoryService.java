package com.techdot.techdot.modules.category;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.main.exception.CategoryCanNotDeleteException;
import com.techdot.techdot.modules.main.exception.CategoryNotExistedException;

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
			.displayOrder(categoryForm.getDisplayOrder())
			.build();

		Category category = categoryRepository.save(newCategory);
		log.info("[{} category-{}]가 정상적으로 생성되었습니다.", category.getId(), category.getName());
	}

	/**
	 * displayOrder에 의해 정렬된 모든 카테고리 가져오기
	 */
	public List<Category> getAllSortedByDisplayOrder() {
		List<Category> categoryList = categoryRepository.findAll();
		Collections.sort(categoryList, Comparator.comparingInt(Category::getDisplayOrder));
		return categoryList;
	}

	/**
	 * viewName으로 카테고리 가져오기
	 * @throws NullPointerException viewName에 해당하는 카테고리가 없을 경우 예외 발생
	 */
	public Category getByViewName(final String viewName) {
		return categoryRepository.findByViewName(viewName)
			.orElseThrow(() -> new CategoryNotExistedException(viewName));
	}

	/**
	 * 카테고리 업데이트 하기
	 * @throws CategoryNotExistedException 1) changeDisplayOrder에 해당하는 카테고리가 없을 경우 예외 발생
	 * @throws CategoryNotExistedException 2) id에 해당하는 카테고리가 없을 경우 예외 발생
	 */
	public void update(final Long id, final CategoryFormDto categoryForm) {
		final int beforeDisplayOrder = categoryForm.getCurDisplayOrder();
		final int changeDisplayOrder = categoryForm.getDisplayOrder();

		// 카테고리 displayOrder 변경한 경우 변경하는 번호에 해당하는 카테고리 displayOrder도 변경
		if (categoryForm.getCurDisplayOrder() != changeDisplayOrder) {
			Category category = categoryRepository.findByDisplayOrder(changeDisplayOrder)
				.orElseThrow(() -> new CategoryNotExistedException("displayOrder: " + changeDisplayOrder));
			category.updateCategoryOrder(beforeDisplayOrder);
		}

		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotExistedException(categoryForm.getViewName()));
		category.update(categoryForm);
	}

	/**
	 * id로 카테고리 삭제하기 (단, 게시글이 존재하는 카테고리는 삭제 불가능)
	 * @throws CategoryCanNotDeleteException 카테고리에 해당하는 게시글이 1개 이상 존재할 경우 예외 발생
	 * @throws CategoryNotExistedException id에 해당하는 게시글이 존재하지 않을 경우 예외 발생
	 */
	public void remove(final Long id) {
		if (categoryRepository.findPostsByCategoryId(id).size() > 0) {
			throw new CategoryCanNotDeleteException("게시글이 존재하는 카테고리는 삭제할 수 없습니다.");
		}

		/**
		 * 삭제시 displayOrder 변경
		 * this.displayOrder = x
		 * 	1. x == size() 가장 맨 뒤 순서일 경우 그냥 삭제
		 * 	2. x가 [1, size()-1]에 해당하는 순서일 경우 x+1 ~ size()에 해당하는 카테고리 displayOrder 전부 업데이트
		 * 	TODO: batch 적용하기
		 */
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotExistedException("id :" + id));
		int displayOrder = category.getDisplayOrder();
		List<Category> categoryList = categoryRepository.findAll();
		if(0 < displayOrder && displayOrder < categoryList.size()){
			for(int i=displayOrder+1; i<=categoryList.size(); i++){
				Category findCategory = categoryRepository.findByDisplayOrder(i)
							.orElseThrow(() -> new CategoryNotExistedException("displayOrder"));
				findCategory.updateCategoryOrder(i-1);
				log.info("[{} category-{}] displayOrder {} update to {}", findCategory.getId(), findCategory.getName(), i ,(i-1));
			}
		}
		categoryRepository.deleteById(id);
		log.info("[{} category-{}]가 정상적으로 삭제되었습니다.", id, category.getName());
	}
}
