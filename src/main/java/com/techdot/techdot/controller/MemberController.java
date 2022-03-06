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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.dto.JoinFormDto;
import com.techdot.techdot.service.MemberService;
import com.techdot.techdot.utils.JoinFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	static final String MEMBER_PROFILE_VIEW_NAME = "member/profile";
	public static final String EMAIL_LOGIN_VIEW_NAME = "member/email-login";

	private final JoinFormValidator joinFormValidator;
	private final MemberService memberService;
	private final MemberRepository memberRepo;

	@InitBinder("joinForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(joinFormValidator);
	}

	@GetMapping("/join")
	public String joinView(Model model) {
		model.addAttribute("joinForm", new JoinFormDto());
		return "member/join";
	}

	@PostMapping("/join")
	public String joinForm(@Valid @ModelAttribute("joinForm") JoinFormDto joinForm,
		Errors errors, Model model) {
		if (errors.hasErrors()) {
			return "member/join";
		}
		Member saveMember = memberService.save(joinForm);
		model.addAttribute("email", saveMember.getEmail());
		return "member/check-email";
	}

	@GetMapping("/confirm-email")
	public String emailConfirm(String token, String email, Model model) {
		String view = "member/confirm-email";
		Member member = memberService.findByEmail(email, view);
		// Optional<Member> opMember = memberRepo.findByEmail(email);
		// if (opMember.isEmpty()) {
		// 	model.addAttribute("error", "해당 이메일은 존재하지 않습니다.");
		// 	return view;
		// }
		// Member member = opMember.get();
		if (!member.isValidToken(token)) {
			model.addAttribute("error", "토큰 정보가 정확하지 않습니다.");
			return view;
		}

		memberService.completeLogin(member);
		model.addAttribute("nickname", member.getNickname());
		return view;
	}

	@GetMapping("/check-email")
	public String checkEmail(@CurrentUser Member member, String email, Model model) {
		if(member.getEmailVerified()){
			return "redirect:/";
		}

		if(member != null){
			model.addAttribute("email", member.getEmail());
			return "member/check-email";
		}

		model.addAttribute("email", email);
		return "member/check-email";
	}

	@GetMapping("/resend-confirm-email/{email}")
	public String resendEmailConfirm(@PathVariable String email, Model model) {
		Member member = memberService.findByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!member.canSendConfirmEmail()) {
			model.addAttribute("error", "잠시 후에 다시 시도해주세요.");
			return "member/check-email";
		}

		memberService.sendConfirmEmail(member);
		return "member/check-email";
	}

	// 패스워드없이 로그인하기
	@GetMapping("/email-login")
	public String emailLoginForm() {
		return EMAIL_LOGIN_VIEW_NAME;
	}

	@PostMapping("/email-login")
	public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
		Member member = memberService.findByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!member.canSendConfirmEmail()) {
			model.addAttribute("error", "잠시 후에 다시 시도해주세요.");
			return EMAIL_LOGIN_VIEW_NAME;
		}

		memberService.sendLoginLink(member);
		attributes.addFlashAttribute("message", "정상적으로 메일이 발송되었습니다.");
		return "redirect:/email-login";
	}


	@GetMapping("/login-by-email")
	public String loginByEmail(String token, String email, Model model) {
		Member member = memberService.findByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!member.isValidToken(token)) {
			model.addAttribute("error", "토큰이 유효하지 않습니다.");
			return EMAIL_LOGIN_VIEW_NAME;
		}

		memberService.login(member);
		return "redirect:/accounts/password";
	}

	@GetMapping("/{nickname}")
	public String profileView(@PathVariable String nickname, Model model, @CurrentUser Member member) {
		Member findMember = memberService.findByNickname(nickname, "redirect:/");

		model.addAttribute("profile", findMember);
		model.addAttribute(member);
		model.addAttribute("isOwner", findMember.equals(member));
		return MEMBER_PROFILE_VIEW_NAME;
	}

}
