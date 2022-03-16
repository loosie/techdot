package com.techdot.techdot.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
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

	@GetMapping("/me/interests")
	public String myInterestsView(@CurrentUser Member member, Model model) {
		model.addAttribute(member);
		return "main/my-interests";
	}

	@GetMapping("/search")
	public String search(@CurrentUser Member member, String keyword, Model model) {
		if(member != null){
			model.addAttribute(member);
		}
		model.addAttribute("keyword", keyword);
		return "search";
	}

	@GetMapping("/error/{status}")
	public String errorView(@PathVariable String status) {
		return "error/" + status;
	}
}
