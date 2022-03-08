package com.techdot.techdot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Member;

@Controller
public class CategoryController {

	@GetMapping("/category/{categoryName}")
	public String home_cs(@PathVariable String categoryName, @CurrentUser Member member, Model model) {
		if (member != null) {
			model.addAttribute(member);
		}
		return CategoryName.valueOf(categoryName).getViewName();
	}
}
