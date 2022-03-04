package com.techdot.techdot.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.PostFormDto;
import com.techdot.techdot.service.PostService;
import com.techdot.techdot.utils.PostFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final PostFormValidator postFormValidator;

	@InitBinder("postForm")
	public void initBinder(WebDataBinder webDataBinder){
		webDataBinder.addValidators(postFormValidator);
	}

	@GetMapping("/new-post")
	public String newPostView(@CurrentUser Member member, Model model){
		model.addAttribute("member", member);
		model.addAttribute("postForm", new PostFormDto());

		return "post/form";
	}


	@PostMapping("/new-post")
	public String newPostForm(@Valid @ModelAttribute("postForm") PostFormDto postForm, Errors errors,
		@CurrentUser Member member, Model model){
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/form";
		}

		postService.post(postForm, member);
		return "redirect:/";
	}

}