package com.techdot.techdot.modules.main;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryService;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final CategoryService categoryService;

	/**
	 * 메인 뷰
	 */
	@GetMapping("/")
	public String mainView(@CurrentUser final Member member, final Model model) {
		if (member != null) {
			model.addAttribute(member);
		}

		model.addAttribute("categoryList", getSortedCategoryList());
		return "index";
	}

	/**
	 * 로그인 뷰
	 */
	@GetMapping("/login")
	public String loginView() {
		return "login";
	}

	/**
	 * 멤버 관심 카테고리 뷰
	 */
	@GetMapping("/me/interests")
	public String myInterestsView(@CurrentUser final Member member, final Model model) {
		if (!member.getEmailVerified()) {
			model.addAttribute("email", member.getEmail());
			return "redirect:/check-email";
		}

		model.addAttribute(member);
		model.addAttribute("categoryList", getSortedCategoryList());
		return "main/my-interests-view";
	}

	/**
	 * 검색 뷰
	 */
	@GetMapping("/search")
	public String searchView(@CurrentUser final Member member, final String keyword, final Model model) {
		if (member != null) {
			model.addAttribute(member);
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryList", getSortedCategoryList());
		return "search";
	}

	/**
	 * 에러 {status} 뷰
	 */
	@GetMapping("/error/{status}")
	public String errorView(@PathVariable final String status) {
		return "error/" + status;
	}

	private List<Category> getSortedCategoryList() {
		List<Category> categoryList = categoryService.getAll();
		Collections.sort(categoryList, Comparator.comparingInt(Category::getDisplayOrder));
		return categoryList;
	}
}
