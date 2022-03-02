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
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.PasswordFormDto;
import com.techdot.techdot.dto.ProfileFormDto;
import com.techdot.techdot.service.MemberService;
import com.techdot.techdot.utils.PasswordFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountsController {

	static final String MEMBER_PROFILE_VIEW_NAME = "member/profile";

	static final String ACCOUNTS_PROFILE_VIEW_NAME = "accounts/profile";
	static final String ACCOUNTS_MAIN_VIEW_URL = "/accounts"; // default: profile

	static final String ACCOUNTS_PASSWORD_VIEW_NAME = "accounts/password";
	static final String ACCOUNTS_PASSWORD_VIEW_URL = ACCOUNTS_MAIN_VIEW_URL + "/password";

	private final MemberRepo memberRepo;
	private final MemberService memberService;

	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder webDataBinder){
		webDataBinder.addValidators(new PasswordFormValidator());
	}

	@GetMapping("/{nickname}")
	public String profileView(@PathVariable String nickname, Model model, @CurrentUser Member member) {
		Optional<Member> opMember = memberRepo.findByNickname(nickname);
		if (opMember.isEmpty()) {
			throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
		}

		Member findMember = opMember.get();
		model.addAttribute("profile", findMember);
		model.addAttribute(member);
		model.addAttribute("isOwner", findMember.equals(member));
		return MEMBER_PROFILE_VIEW_NAME;
	}

	@GetMapping(ACCOUNTS_MAIN_VIEW_URL)
	public String profileSettingView(Model model, @CurrentUser Member member) {
		model.addAttribute(member);
		model.addAttribute("profileForm", new ProfileFormDto(member));
		return ACCOUNTS_PROFILE_VIEW_NAME;
	}

	@PostMapping(ACCOUNTS_MAIN_VIEW_URL)
	public String profileSettingForm(@Valid @ModelAttribute("profileForm") ProfileFormDto profileForm, Errors errors,
		Model model, @CurrentUser Member member, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return ACCOUNTS_PROFILE_VIEW_NAME;
		}

		memberService.updateProfile(member, profileForm);
		model.addAttribute("member", new ProfileFormDto(member));
		redirectAttributes.addFlashAttribute("message", "프로필이 정상적으로 저장되었습니다.");
		return "redirect:" + ACCOUNTS_MAIN_VIEW_URL;
	}

	@GetMapping(ACCOUNTS_PASSWORD_VIEW_URL)
	public String passwordSettingView(Model model, @CurrentUser Member member) {
		model.addAttribute(member);
		model.addAttribute("passwordForm", new PasswordFormDto());
		return ACCOUNTS_PASSWORD_VIEW_NAME;
	}

	@PostMapping(ACCOUNTS_PASSWORD_VIEW_URL)
	public String passwordSettingForm(@Valid @ModelAttribute("passwordForm") PasswordFormDto passwordForm, Errors errors,
		Model model, @CurrentUser Member member, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors() || !passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
			model.addAttribute(member);
			return ACCOUNTS_PASSWORD_VIEW_NAME;
		}

		memberService.updatePassword(member, passwordForm);
		model.addAttribute(member);
		model.addAttribute("passwordForm", new PasswordFormDto());
		redirectAttributes.addFlashAttribute("message", "비밀번호가 정상적으로 변경되었습니다.");
		return "redirect:" + ACCOUNTS_PASSWORD_VIEW_URL;
	}
}
