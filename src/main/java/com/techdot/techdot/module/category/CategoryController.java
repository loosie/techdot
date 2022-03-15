package com.techdot.techdot.module.category;

import static com.techdot.techdot.module.category.CategoryName.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.member.auth.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {
	@GetMapping("/category/{categoryName}")
	public String homeByCategory(@PathVariable String categoryName, @CurrentUser Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		if (member != null) {
			model.addAttribute(member);
		}
		model.addAttribute("sortProperty",
			pageable.getSort().toString().contains("uploadDateTime") ? "uploadDateTime" : "id");
		return getMainViewName(categoryName);
	}
}
