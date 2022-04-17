package com.techdot.techdot.modules.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.modules.main.CategoryCanNotDeleteException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	private CategoryService categoryService;
	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryService(categoryRepository);
	}

	@DisplayName("카테고리 id로 삭제하기")
	@Test
	void removeCategoryById_Success(){
		// given
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of());

		// when
		categoryService.remove(1L);

		// then
		then(categoryRepository).should(times(1)).findPostsByCategoryId(any());
		assertTrue(categoryRepository.findById(1L).isEmpty());
	}

	@DisplayName("카테고리 id로 삭제하기 실패 - 해당 카테고리에 게시글이 존재할 경우")
	@Test
	void removeCategoryById_CategoryPostsIsExisted_ExceptionThrown(){
		// given
		given(categoryRepository.findPostsByCategoryId(1L)).willReturn(List.of(1L));

		// when, then
		assertThrows(CategoryCanNotDeleteException.class, () -> categoryService.remove(1L));
	}
}