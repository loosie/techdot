package com.techdot.techdot.module.category;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.techdot.techdot.infra.MockMvcTest;

@MockMvcTest
class CategoryControllerTest {

	@Autowired private MockMvc mockMvc;

	@DisplayName("카테고리 별로 뷰 보여주기")
	@Test
	void categoryView() {
		Arrays.stream(CategoryName.values()).forEach(categoryName -> {
			try {
				mockMvc.perform(get("/category/" + categoryName.getViewName()))
					.andExpect(status().isOk())
					.andExpect(view().name(CategoryName.getMainViewName(categoryName.getViewName())))
					.andExpect(unauthenticated());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
}
