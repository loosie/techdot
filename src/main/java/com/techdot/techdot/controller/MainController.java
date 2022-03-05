package com.techdot.techdot.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
	public String home(@CurrentUser Member member, Model model,
		@PageableDefault(size = 16, page = 0, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
		model.addAttribute("postList", postRepository.findAll(pageable));
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
