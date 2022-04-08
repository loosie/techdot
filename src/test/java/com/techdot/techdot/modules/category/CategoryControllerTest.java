package com.techdot.techdot.modules.category;

import static com.techdot.techdot.infra.Constant.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.infra.MockMvcTest;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@MockMvcTest
class CategoryControllerTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private CategoryService categoryService;
	@Autowired private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp(){
		categoryRepository.save(Category.builder().viewName("java").title("JAVA").name("자바").build());
	}

	@DisplayName("카테고리 별로 뷰")
	@Test
	void categoryView() throws Exception {
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
	void categoryCreateView() throws Exception {
		mockMvc.perform(get("/new-category"))
			.andExpect(status().isOk())
			.andExpect(status().isOk())
			.andExpect(view().name("category/form"))
			.andExpect(model().attributeExists("member"))
			.andExpect(model().attributeExists("categoryForm"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 생성 성공")
	@Test
	void createNewCategory_success() throws Exception {
		mockMvc.perform(post("/new-category")
			.param("name", "nav 이름")
			.param("title", "메인 타이틀")
			.param("viewName", "view")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/accounts/settings/category"))
			.andExpect(authenticated());
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("카테고리 생성 실패 - viewName 입력값 오류")
	@Test
	void createNewCategory_fail_invalidViewName() throws Exception {
		mockMvc.perform(post("/new-category")
			.param("name", "nav 이름")
			.param("title", "메인 타이틀")
			.param("viewName", "viewName오류")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(view().name("category/form"))
			.andExpect(authenticated());
	}


}
