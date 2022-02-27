package com.techdot.techdot.controller;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.techdot.techdot.dto.MemberJoinFormDto;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.service.MemberService;
import com.techdot.techdot.utils.JoinFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final JoinFormValidator joinFormValidator;
	private final MemberService memberService;


	@InitBinder("joinForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(joinFormValidator);
	}

	@GetMapping("/join")
	public String memberJoinForm(Model model) {
		model.addAttribute("joinForm", new MemberJoinFormDto());
		return "member/join";
	}

	@PostMapping("/join")
	public String memberJoinFormRequest(@Valid @ModelAttribute("joinForm") MemberJoinFormDto joinForm, Errors errors) {
		if (errors.hasErrors()) {
			return "member/join";
		}

		memberService.save(joinForm);

		return "redirect:/";

	}


}
