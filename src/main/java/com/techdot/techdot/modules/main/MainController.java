package com.techdot.techdot.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.modules.category.CategoryService;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.post.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final CategoryService categoryService;
	/**
	 * 메인 뷰
	 * @param member
	 * @param model
	 * @return
	 */
	@GetMapping("/")
	public String home(@CurrentUser final Member member, Model model) {
		if (member != null) {
			if (!member.getEmailVerified()) {
				model.addAttribute("email", member.getEmail());
				return "redirect:/check-email";
			}
			model.addAttribute(member);
		}
		model.addAttribute("categoryList", categoryService.getAll());
		return "index";
	}

	/**
	 * 로그인 뷰
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	/**
	 * 멤버 관심 카테고리 뷰
	 * @param member
	 * @param model
	 * @return
	 */
	@GetMapping("/me/interests")
	public String myInterestsView(@CurrentUser final Member member, Model model) {
		model.addAttribute(member);
		return "main/my-interests";
	}

	/**
	 * 검색 뷰
	 * @param member
	 * @param keyword
	 * @param model
	 * @return
	 */
	@GetMapping("/search")
	public String search(@CurrentUser final Member member, final String keyword, final Model model) {
		if(member != null){
			model.addAttribute(member);
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryList", categoryService.getAll());
		return "search";
	}

	/**
	 * 에러 {stauts} 뷰
	 * @param status
	 * @return
	 */
	@GetMapping("/error/{status}")
	public String errorView(@PathVariable final String status) {
		return "error/" + status;
	}

}
