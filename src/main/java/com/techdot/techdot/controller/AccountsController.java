package com.techdot.techdot.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PasswordFormDto;
import com.techdot.techdot.dto.ProfileFormDto;
import com.techdot.techdot.service.MemberService;
import com.techdot.techdot.service.PostService;
import com.techdot.techdot.utils.PasswordFormValidator;
import com.techdot.techdot.utils.ProfileFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountsController {


	static final String ACCOUNTS_PROFILE_VIEW_NAME = "accounts/profile";
	static final String ACCOUNTS_MAIN_VIEW_URL = "/accounts"; // default: profile

	static final String ACCOUNTS_PASSWORD_VIEW_NAME = "accounts/password";
	static final String ACCOUNTS_PASSWORD_VIEW_URL = ACCOUNTS_MAIN_VIEW_URL + "/password";

	static final String ACCOUNTS_SETTING_VIEW_NAME = "accounts/settings";
	static final String ACCOUNTS_SETTING_VIEW_URL = "/settings";

	private final MemberService memberService;
	private final PostService postService;
	private final ProfileFormValidator profileFormValidator;

	@InitBinder("passwordForm")
	public void pwInitBinder(WebDataBinder webDataBinder){
		webDataBinder.addValidators(new PasswordFormValidator());
	}

	@InitBinder("profileForm")
	public void profileInitBinder(WebDataBinder webDataBinder){
		webDataBinder.addValidators(profileFormValidator);
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

	@GetMapping(ACCOUNTS_SETTING_VIEW_URL)
	public String accountsSettingView(Model model, @CurrentUser Member member) {
		model.addAttribute(member);
		return ACCOUNTS_SETTING_VIEW_NAME;
	}

	@GetMapping("/my-upload")
	public String myUploadPostsView(@CurrentUser Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		Page<Post> postPage = postService.findByManager(member, pageable);

		String sortProperty = "id";
		model.addAttribute(member);
		model.addAttribute("postPage", postPage);
		model.addAttribute("sortProperty", sortProperty);
		return "accounts/my-upload";
	}


}
