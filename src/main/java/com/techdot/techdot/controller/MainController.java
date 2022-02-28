package com.techdot.techdot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;

@Controller
public class MainController {

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model){
		if(member != null){
			model.addAttribute(member);
		}
		return "index";
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}
}
