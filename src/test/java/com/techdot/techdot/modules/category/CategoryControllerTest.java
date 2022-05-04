package com.techdot.techdot.modules.category;

import static com.techdot.techdot.infra.Constant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@MockMvcTest
class CategoryControllerTest extends AbstractContainerBaseTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private CategoryService categoryService;
	@Autowired private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp(){
		categoryRepository.save(Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
			.displayOrder(1)
			.build());
	}

	@DisplayName("카테고리 별로 뷰")
	@Test
	void categoryView_PathVariableIsViewName_Success() throws Exception {
		Category category = categoryService.getByViewName("java");

		mockMvc.perform(get("/category/" + category.getViewName()))
			.andExpect(status().isOk())
			.andExpect(view().name("main/category-view"))
			.andExpect(model().attributeExists("category"))
			.andExpect(model().attributeExists("categoryList"))
			.andExpect(model().attributeExists("sortProperty"))
			.andExpect(unauthenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 생성 뷰")
	@Test
	void categoryCreateView_Success() throws Exception {
		mockMvc.perform(get("/new-category"))
			.andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(view().name("category/form"))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("categoryForm"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 생성 폼 요청 성공")
	@Test
	void categoryCreateForm_Success() throws Exception {
		mockMvc.perform(post("/new-category")
			.param("name", "nav 이름")
			.param("title", "메인 타이틀")
			.param("viewName", "view")
			.param("displayOrder", "1")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/settings/category"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 생성 폼 요청 실패 - viewName 입력값 오류")
	@Test
	void categoryCreateForm_ViewNameIsNotValidType_Error() throws Exception {
		mockMvc.perform(post("/new-category")
			.param("name", "nav 이름")
			.param("title", "메인 타이틀")
			.param("viewName", "viewName오류")
			.param("displayOrder", "1")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("category/form"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 수정하기 폼 요청 성공")
	@Test
	void categoryUpdateForm_Success() throws Exception {
		// given
		Category category = categoryService.getByViewName("java");

		// when, then
		mockMvc.perform(post("/category/" + category.getId() + "/edit")
			.param("title", "Java title 222")
			.param("name", "자바 222")
			.param("viewName", "java-2")
			.param("displayOrder", "1")
			.param("curTitle", category.getTitle())
			.param("curName", category.getName())
			.param("curViewName", category.getViewName())
			.param("curDisplayOrder", ""+category.getDisplayOrder())
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"))
			.andExpect(authenticated());

		// then
		Category changeCategory = categoryRepository.findById(category.getId()).get();
		assertEquals("Java title 222", changeCategory.getTitle());
		assertEquals("java-2", changeCategory.getViewName());
		assertEquals("자바 222", changeCategory.getName());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 수정하기 폼 요청 실패 - 데이터 중복")
	@Test
	void categoryUpdateForm_ViewNameIsDuplicatedData_Error() throws Exception {
		// given
		categoryRepository.save(Category.builder()
			.viewName("backend")
			.title("BACKEND")
			.name("백엔드")
			.displayOrder(1)
			.build());
		Category category = categoryService.getByViewName("java");

		// when, then
		mockMvc.perform(post("/category/" + category.getId() + "/edit")
			.param("title", "BACKEND...")
			.param("name", "백엔드...")
			.param("viewName", "backend") // 중복
			.param("displayOrder", "1")
			.param("curTitle", category.getTitle())
			.param("curName", category.getName())
			.param("curViewName", category.getViewName())
			.param("curDisplayValue", ""+category.getDisplayOrder())
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("member"))
			.andExpect(view().name("category/updateForm"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 삭제하기 성공")
	@Test
	void categoryRemove_Success() throws Exception {
		// given
		Category category = categoryService.getByViewName("java");

		// when, then
		mockMvc.perform(post("/category/" + category.getId() + "/remove")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/settings/category"))
			.andExpect(authenticated());
	}


}
