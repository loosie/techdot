package com.techdot.techdot.modules.category;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.category.validator.CategoryFormValidator;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryFormValidator categoryFormValidator;

	@InitBinder("categoryForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(categoryFormValidator);
	}


	/**
	 * 카테고리별 게시글 뷰
	 * 쿼리 발생 횟수 : 3
	 * viewName으로 카테고리 조회 + 전체 카테고리 조회 (nav) + 카테고리별 게시글 조회
	 */
	@GetMapping("/category/{viewName}")
	public String categoryView(@PathVariable final String viewName, @CurrentUser final Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		if (member != null) {
			model.addAttribute(member);
		}

		Category category = categoryService.getByViewName(viewName);
		model.addAttribute("category", category);
		model.addAttribute("categoryList", categoryService.getAll());
		model.addAttribute("sortProperty",
			pageable.getSort().toString().contains("uploadDateTime") ? "uploadDateTime" : "createdDateTime");
		return "main/category-view";
	}

	/**
	 * (ADMIN) 카테고리 생성 뷰
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/new-category")
	public String newCategoryView(@CurrentUser final Member member, Model model) {
		model.addAttribute("member", member);
		model.addAttribute("categoryForm", new CategoryFormDto());

		return "category/form";
	}

	/**
	 * (ADMIN) 카테고리 생성 요청
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/new-category")
	public String newCategoryForm(@Valid @ModelAttribute("categoryForm") final CategoryFormDto categoryForm,
		Errors errors, @CurrentUser final Member member, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "category/form";
		}

		categoryService.save(categoryForm);
		return "redirect:/accounts/settings/category";
	}

	/**
	 * (ADMIN) 카테고리 업데이트 뷰
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/category/{id}/edit")
	public String updatePostView(@PathVariable final Long id, @CurrentUser final Member member, Model model) {
		Category category = categoryService.getById(id);

		model.addAttribute(member);
		model.addAttribute("categoryId", id);
		model.addAttribute("categoryForm", new CategoryFormDto(category));
		return "category/updateForm";
	}

	/**
	 * (ADMIN) 카테고리 업데이트 요청
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/category/{id}/edit")
	public String updatePostForm(@PathVariable Long id, @Valid @ModelAttribute("categoryForm") final CategoryFormDto categoryForm,
		Errors errors, @CurrentUser final Member member, Model model, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "category/updateForm";
		}

		categoryService.update(id, categoryForm);
		redirectAttributes.addFlashAttribute("message", "카테고리가 정상적으로 수정되었습니다.");
		return "redirect:/category/" + id + "/edit";
	}

	/**
	 * (ADMIN) 카테고리 삭제
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/category/{id}/remove")
	public String removePostForm(@PathVariable Long id) {
		categoryService.remove(id);
		return "redirect:/accounts/settings/category";
	}
}
