package com.techdot.techdot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.techdot.techdot.controller.dto.MemberJoinRequestDto;

@Controller
public class MemberController {

	@GetMapping("/join")
	public String joinForm(Model model){
		model.addAttribute("joinForm", new MemberJoinRequestDto());
		return "member/join";
	}
}
