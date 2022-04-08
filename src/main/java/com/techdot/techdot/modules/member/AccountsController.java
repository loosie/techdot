package com.techdot.techdot.modules.member;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.member.dto.PasswordFormDto;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.post.PostService;
import com.techdot.techdot.modules.member.validator.PasswordFormValidator;
import com.techdot.techdot.modules.member.validator.ProfileFormValidator;
import com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountsController {

	static final String ACCOUNTS_PROFILE_VIEW_NAME = "accounts/profile";
	static final String ACCOUNTS_MAIN_VIEW_URL = ""; // default: profile

	static final String ACCOUNTS_PASSWORD_VIEW_NAME = "accounts/password";
	static final String ACCOUNTS_PASSWORD_VIEW_URL = "/change-password";

	static final String ACCOUNTS_SETTING_VIEW_NAME = "accounts/settings";
	static final String ACCOUNTS_SETTING_VIEW_URL = "/settings";

	static final String ACCOUNTS_MY_UPLOAD_VIEW_NAME = "accounts/my-upload";
	static final String ACCOUNTS_MY_UPLOAD_VIEW_URL = "/my-upload";

	static final String ACCOUNTS_CATEGORY_VIEW_NAME = "accounts/category";
	static final String ACCOUNTS_CATEGORY_VIEW_URL = "/settings/category";

	private final MemberService memberService;
	private final PostService postService;
	private final CategoryRepository categoryRepository;
	private final ProfileFormValidator profileFormValidator;

	@InitBinder("passwordForm")
	public void pwInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidator());
	}

	@InitBinder("profileForm")
	public void profileInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(profileFormValidator);
	}

	/**
	 * 개인정보설정 메인 뷰 (프로필)
	 * @param model
	 * @param member
	 * @return
	 */
	@GetMapping(ACCOUNTS_MAIN_VIEW_URL)
	public String profileSettingView(Model model, @CurrentUser final Member member) {
		model.addAttribute(member);
		model.addAttribute("profileForm", new ProfileFormDto(member));
		return ACCOUNTS_PROFILE_VIEW_NAME;
	}

	/**
	 * 계정 프로필 변경 요청
	 * @param profileForm
	 * @param errors
	 * @param model
	 * @param member
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping(ACCOUNTS_MAIN_VIEW_URL)
	public String profileSettingForm(@Valid @ModelAttribute("profileForm") final ProfileFormDto profileForm,
		Errors errors,
		Model model, @CurrentUser final Member member, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return ACCOUNTS_PROFILE_VIEW_NAME;
		}

		memberService.updateProfile(member, profileForm);
		model.addAttribute("member", new ProfileFormDto(member));
		redirectAttributes.addFlashAttribute("message", "프로필이 정상적으로 저장되었습니다.");
		return "redirect:/accounts";
	}

	/**
	 * 계정 비밀번호 변경 뷰
	 * @param model
	 * @param member
	 * @return
	 */
	@GetMapping(ACCOUNTS_PASSWORD_VIEW_URL)
	public String passwordSettingView(Model model, @CurrentUser final Member member) {
		model.addAttribute(member);
		model.addAttribute("passwordForm", new PasswordFormDto());
		return ACCOUNTS_PASSWORD_VIEW_NAME;
	}

	/**
	 * 계정 비밀번호 변경 요청
	 * @param passwordForm
	 * @param errors
	 * @param model
	 * @param member
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping(ACCOUNTS_PASSWORD_VIEW_URL)
	public String passwordSettingForm(@Valid @ModelAttribute("passwordForm") final PasswordFormDto passwordForm,
		Errors errors,
		Model model, @CurrentUser final Member member, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors() || !passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
			model.addAttribute(member);
			return ACCOUNTS_PASSWORD_VIEW_NAME;
		}

		memberService.updatePassword(member, passwordForm);
		model.addAttribute(member);
		model.addAttribute("passwordForm", new PasswordFormDto());
		redirectAttributes.addFlashAttribute("message", "비밀번호가 정상적으로 변경되었습니다.");
		return "redirect:/accounts" + ACCOUNTS_PASSWORD_VIEW_URL;
	}

	/**
	 * 계정 설정 뷰
	 * @param model
	 * @param member
	 * @return
	 */
	@GetMapping(ACCOUNTS_SETTING_VIEW_URL)
	public String accountsSettingView(Model model, @CurrentUser final Member member) {
		model.addAttribute(member);
		return ACCOUNTS_SETTING_VIEW_NAME;
	}

	/**
	 * (ADMIN)계정이 업로드한 게시글 뷰
	 * 쿼리 발생 횟수 : 2 - 게시글 조회 쿼리 + 카운트 쿼리
	 * @param member
	 * @param model
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(ACCOUNTS_MY_UPLOAD_VIEW_URL)
	public String myUploadPostsView(@CurrentUser final Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "uploadDateTime", direction = Sort.Direction.DESC) final Pageable pageable) {
		Page<MyUploadPostResponseDto> postPage = postService.getByManager(member, pageable);

		model.addAttribute(member);
		model.addAttribute("postPage", postPage);
		model.addAttribute("sortProperty",
			pageable.getSort().toString().contains("uploadDateTime") ? "uploadDateTime" : "createdDateTime");
		return ACCOUNTS_MY_UPLOAD_VIEW_NAME;
	}

	/**
	 * (ADMIN) 카테고리 설정 뷰
	 * @param member
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(ACCOUNTS_CATEGORY_VIEW_URL)
	public String categorySettingsView(@CurrentUser final Member member, Model model) {
		List<Category> categoryList = categoryRepository.findAll();
		model.addAttribute("categoryList", categoryList);
		model.addAttribute(member);
		return ACCOUNTS_CATEGORY_VIEW_NAME;
	}

}
