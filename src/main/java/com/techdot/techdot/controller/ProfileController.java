package com.techdot.techdot.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.MemberRepo;
import com.techdot.techdot.dto.ProfileFormDto;
import com.techdot.techdot.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProfileController {

	private static final String PROFILE_VIEW_NAME = "settings/profile";
	private static final String PROFILE_VIEW_URL = "/profile";

	private final MemberRepo memberRepo;
	private final MemberService memberService;

	@GetMapping("/{nickname}/profile")
	public String profile(@PathVariable String nickname, Model model, @CurrentUser Member member) {
		Optional<Member> opMember = memberRepo.findByNickname(nickname);
		if (opMember.isEmpty()) {
			throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
		}

		Member findMember = opMember.get();
		if(!findMember.equals(member)){
			throw new AccessDeniedException(nickname +" 접근 권한이 없습니다.");
		}

		model.addAttribute("member", member);
		return PROFILE_VIEW_NAME;
	}

	@PostMapping("/profile")
	public String profileForm(@Valid @ModelAttribute("profileForm") ProfileFormDto profileForm, Errors errors,
		Model model, @CurrentUser Member member, RedirectAttributes rttr) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return PROFILE_VIEW_NAME;

		}

		memberService.updateProfile(member, profileForm);
		model.addAttribute("profile", new ProfileFormDto(member));
		rttr.addFlashAttribute("message", "프로필이 정상적으로 저장되었습니다.");
		return "redirect:/" + member.getNickname() + PROFILE_VIEW_URL;
	}
}
