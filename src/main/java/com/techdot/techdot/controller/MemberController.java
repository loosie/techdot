package com.techdot.techdot.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.JoinFormDto;
import com.techdot.techdot.service.MemberService;
import com.techdot.techdot.utils.JoinFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final JoinFormValidator joinFormValidator;
	private final MemberService memberService;
	private final MemberRepo memberRepo;


	@InitBinder("joinForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(joinFormValidator);
	}

	@GetMapping("/join")
	public String joinForm(Model model) {
		model.addAttribute("joinForm", new JoinFormDto());
		return "member/join";
	}

	@PostMapping("/join")
	public String joinFormRequest(@Valid @ModelAttribute("joinForm") JoinFormDto joinForm, Errors errors) {
		if (errors.hasErrors()) {
			return "member/join";
		}
		Member saveMember = memberService.save(joinForm);
		memberService.login(saveMember);
		return "redirect:/";
	}

	@GetMapping("/email-confirm")
	public String checkEmailToken(String token, String email, Model model){
		Optional<Member> opMember = memberRepo.findByEmail(email);
		String view = "member/email-confirm";
		if(opMember.isEmpty()){
			model.addAttribute("error", "해당 이메일은 존재하지 않습니다.");
			return view;
		}

		Member member = opMember.get();
		if(!member.isSameToken(token)){
			model.addAttribute("error", "토큰 정보가 정확하지 않습니다.");
			return view;
		}

		member.completeEmailVerified();
		memberService.login(member);
		model.addAttribute("nickname", member.getNickname());
		return view;
	}
}
