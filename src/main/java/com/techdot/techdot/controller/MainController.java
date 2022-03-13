package com.techdot.techdot.controller;

import static com.techdot.techdot.domain.CategoryName.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;

@Controller
public class MainController {

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model) {
		if (member != null) {
			if (!member.getEmailVerified()) {
				model.addAttribute("email", member.getEmail());
				return "redirect:/check-email";
			}
			model.addAttribute(member);
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/category/{categoryName}")
	public String homeByCategory(@PathVariable String categoryName, @CurrentUser Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		if (member != null) {
			if (!member.getEmailVerified()) {
				model.addAttribute("email", member.getEmail());
				return "redirect:/check-email";
			}
			model.addAttribute(member);
		}
		model.addAttribute("sortProperty", pageable.getSort().toString().contains("uploadDateTime") ? "uploadDateTime" : "id");
		return getMainViewName(categoryName);
	}

	@GetMapping("/me/interests")
	public String MyInterestsView(@CurrentUser Member member, Model model) {
		model.addAttribute(member);
		return "main/my-interests";
	}
}
