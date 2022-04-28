package com.techdot.techdot.modules.member;

import static com.techdot.techdot.modules.member.dao.AuthDao.TokenType.*;

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

import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.member.dto.JoinFormDto;
import com.techdot.techdot.modules.member.validator.JoinFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	static final String ME_LIKES_VIEW_NAME = "member/likes";
	static final String EMAIL_LOGIN_VIEW_NAME = "member/email-login";
	static final String CHECK_EMAIL_VIEW_NAME = "member/check-email";

	private final JoinFormValidator joinFormValidator;
	private final MemberService memberService;

	@InitBinder("joinForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(joinFormValidator);
	}

	/**
	 * 회원가입 뷰
	 */
	@GetMapping("/join")
	public String joinView(Model model) {
		model.addAttribute("joinForm", new JoinFormDto());
		return "member/join";
	}

	/**
	 * 회원가입 요청
	 */
	@PostMapping("/join")
	public String joinForm(@Valid @ModelAttribute("joinForm") final JoinFormDto joinForm,
		Errors errors, Model model) {
		if (errors.hasErrors()) {
			return "member/join";
		}
		Member saveMember = memberService.save(joinForm);
		model.addAttribute("email", saveMember.getEmail());
		return CHECK_EMAIL_VIEW_NAME;
	}

	/**
	 * 이메일 인증 확인 링크
	 */
	@GetMapping("/confirm-email")
	public String confirmEmailLink(final String token, final String email, Model model) {
		String view = "member/confirm-email";
		Member member = memberService.getByEmail(email, view);

		if (!memberService.isValidAuthToken(member.getId(), token, EMAIL)) {
			model.addAttribute("message", "토큰 정보가 정확하지 않습니다.");
			return view;
		}

		memberService.completeLogin(member);
		model.addAttribute("nickname", member.getNickname());
		return view;
	}

	/**
	 * 이메일 인증 완료 뷰
	 */
	@GetMapping("/check-email")
	public String checkEmail(@CurrentUser final Member member, final String email, Model model) {
		if (memberService.isAuthUser(member)) {
			return "redirect:/";
		}

		// 로그인 상태에서 인증 완료
		if (member != null) {
			model.addAttribute("email", member.getEmail());
			return CHECK_EMAIL_VIEW_NAME;
		}

		// 회원가입 후 인증 완료
		model.addAttribute("email", email);
		return CHECK_EMAIL_VIEW_NAME;
	}

	/**
	 * 인증 메일 재전송 뷰
	 */
	@GetMapping("/resend-confirm-email/{email}")
	public String resendEmailConfirm(@PathVariable final String email, Model model) {
		Member member = memberService.getByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!memberService.checkIsAvailableSendEmail(member)) {
			model.addAttribute("error", "잠시 후에 다시 시도해주세요.");
			return CHECK_EMAIL_VIEW_NAME;
		}

		memberService.sendConfirmEmail(member);
		model.addAttribute("message", "이메일 재전송이 완료되었습니다.");
		return CHECK_EMAIL_VIEW_NAME;
	}

	/**
	 * 패스워드 없이 로그인 하기 뷰
	 */
	@GetMapping("/email-login")
	public String emailLoginView() {
		return EMAIL_LOGIN_VIEW_NAME;
	}

	/**
	 *  패스워드 없이 로그인 링크 보내기
	 */
	@PostMapping("/email-login")
	public String sendEmailLoginLink(final String email, Model model, RedirectAttributes attributes) {
		Member member = memberService.getByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!memberService.checkIsAvailableSendEmail(member)) {
			model.addAttribute("error", "잠시 후에 다시 시도해주세요.");
			return EMAIL_LOGIN_VIEW_NAME;
		}

		memberService.sendLoginLink(member);
		attributes.addFlashAttribute("message", "정상적으로 메일이 발송되었습니다.");
		return "redirect:/email-login";
	}

	/**
	 * 패스워드 없이 이메일로 로그인 링크 체크
	 */
	@GetMapping("/login-by-email")
	public String loginByEmail(final String token, final String email, Model model) {
		Member member = memberService.getByEmail(email, EMAIL_LOGIN_VIEW_NAME);

		if (!memberService.isValidAuthToken(member.getId(), token, LOGIN)) {
			model.addAttribute("error", "토큰이 유효하지 않습니다.");
			return EMAIL_LOGIN_VIEW_NAME;
		}

		if (!member.getEmailVerified()) {
			// 이메일 인증 처리 완료
			memberService.completeLogin(member);
		} else {
			memberService.login(member);
		}

		return "redirect:/accounts/change-password";
	}

	/**
	 * 멤버가 좋아하는 게시글 뷰
	 */
	@GetMapping("/me/likes")
	public String MyLikesView(@CurrentUser final Member member, Model model) {
		model.addAttribute(member);
		return ME_LIKES_VIEW_NAME;
	}

}
