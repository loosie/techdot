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
			.viewName("java").title("자바").name("Java").build();
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
			.viewName("java").title("자바").name("Java").build();
		CategoryFormDto categoryFormDto = new CategoryFormDto();
		categoryFormDto.setViewName("changeJava");
		given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

		// when
		categoryService.update(1L, categoryFormDto);

		// then
		then(categoryRepository).should(times(1)).findById(any());
		assertEquals(category.getViewName(), "changeJava");
	}

	@DisplayName("카테고리 id로 삭제하기")
	@Test
	void categoryRemove_Success(){
		// given
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of());

		// when
		categoryService.remove(1L);

		// then
		then(categoryRepository).should(times(1)).findPostsByCategoryId(any());
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