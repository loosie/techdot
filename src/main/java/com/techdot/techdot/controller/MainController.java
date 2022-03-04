package com.techdot.techdot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final PostRepository postRepository;

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model){
		model.addAttribute("postList", postRepository.findAll());
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
