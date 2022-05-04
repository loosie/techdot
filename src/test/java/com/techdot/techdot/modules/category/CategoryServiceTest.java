package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.main.exception.CategoryCanNotDeleteException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	private CategoryService categoryService;
	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@DisplayName("카테고리 저장하기 성공")
	@Test
	void categorySave_Success(){
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();
		CategoryFormDto categoryFormDto = new CategoryFormDto(category);
		given(categoryRepository.save(category)).willReturn(category);

		// when
		categoryService.save(categoryFormDto);

		// then
		then(categoryRepository).should(times(1)).save(any());
	}

	@DisplayName("카테고리 업데이트하기 성공")
	@Test
	void categoryUpdate_Success(){
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();
		CategoryFormDto categoryFormDto = new CategoryFormDto();
		categoryFormDto.setViewName("changeJava");
		given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

		// when
		categoryService.update(1L, categoryFormDto);

		// then
		then(categoryRepository).should(times(1)).findById(any());
		assertEquals(category.getViewName(), "changeJava");
	}

	@DisplayName("카테고리 업데이트하기 성공 - displayOrder 변경할 경우 대상 category 값도 변경")
	@Test
	void categoryUpdate_IfDisplayOrderIsChanged_ThenTargetCategoryValueIsAlsoChanged_Success(){
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();
		Category category2 = Category.builder()
			.viewName("java2").title("자바2").name("Java2").displayOrder(2).build();
		CategoryFormDto categoryFormDto = new CategoryFormDto(category);
		categoryFormDto.setViewName("changeJava");
		categoryFormDto.setDisplayOrder(2);
		given(categoryRepository.findByDisplayOrder(2)).willReturn(Optional.of(category2));
		given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

		// when
		categoryService.update(1L, categoryFormDto);

		// then
		then(categoryRepository).should(times(1)).findById(any());
		assertEquals(category.getViewName(), "changeJava");
		assertEquals(category.getDisplayOrder(), 2);
	}

	@DisplayName("카테고리 id로 삭제하기 - displayOrder가 size()와 일치하는 경우 갱신없이 바로 삭제")
	@Test
	void categoryRemove_IfDisplayOrderIsSize_ThenOtherCategoryDisplayOrderKeep_Success(){
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of());
		given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
		given(categoryRepository.findAll()).willReturn(List.of(category));

		// when
		categoryService.remove(1L);

		// then
		then(categoryRepository).should(times(1)).findPostsByCategoryId(any());
		then(categoryRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findAll();
		then(categoryRepository).should(times(1)).deleteById(any());
	}

	@DisplayName("카테고리 id로 삭제하기 - displayOrder가 [1, size()-1] 사이에 있는 경우 [displayOrder+1, size()] 값 모두 -1로 갱신하고 삭제")
	@Test
	void categoryRemove_IfDisplayOrderIsOneAndSizeMinusOne_ThenOtherCategoryDisplayOrderMinusOne_Success(){
		// given
		Category category = Category.builder()
			.viewName("java").title("자바").name("Java").displayOrder(1).build();
		Category category2 = Category.builder()
			.viewName("java2").title("자바2").name("Java2").displayOrder(2).build();
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of());
		given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
		given(categoryRepository.findAll()).willReturn(List.of(category, category2));
		given(categoryRepository.findByDisplayOrder(2)).willReturn(Optional.of(category2));

		// when
		categoryService.remove(1L);

		// then
		then(categoryRepository).should(times(1)).findPostsByCategoryId(any());
		then(categoryRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findAll();
		then(categoryRepository).should(times(1)).deleteById(any());
	}

	@DisplayName("카테고리 id로 삭제하기 실패 - 해당 카테고리에 게시글이 존재할 경우")
	@Test
	void categoryRemove_PostIsExistedInCategory_ExceptionThrown(){
		// given
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of(1L));

		// when, then
		assertThrows(CategoryCanNotDeleteException.class, () -> categoryService.remove(1L));
	}
}