package com.techdot.techdot.controller;

import java.util.List;

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
import com.techdot.techdot.domain.Post;
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
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(postFormValidator);
	}

	@GetMapping("/new-post")
	public String newPostView(@CurrentUser Member member, Model model) {
		model.addAttribute("member", member);
		model.addAttribute("postForm", new PostFormDto());

		return "post/form";
	}

	@PostMapping("/new-post")
	public String newPostForm(@Valid @ModelAttribute("postForm") PostFormDto postForm, Errors errors,
		@CurrentUser Member member, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/form";
		}

		postService.post(postForm, member);
		return "redirect:/";
	}

	@GetMapping("/my-upload")
	public String myUploadPostsView(@CurrentUser Member member, Model model) {
		List<Post> postList = postService.getMemberPosts(member.getNickname());
		model.addAttribute(member);
		model.addAttribute("postList", postList);
		return "accounts/my-upload";
	}

	@GetMapping("/post/{id}/edit")
	public String updatePostView(@PathVariable Long id, @CurrentUser Member member, Model model) {
		Post post = postService.getPostById(id);
		if(!post.isManager(member)){
			return "redirect:/";
		}
		model.addAttribute(member);
		model.addAttribute("postId", id);
		model.addAttribute("postForm", new PostFormDto(post));
		return "post/updateForm";
	}

	@PostMapping("/post/{id}/edit")
	public String updatePostForm(@PathVariable Long id, @Valid @ModelAttribute("postForm") PostFormDto postForm,
		Errors errors, @CurrentUser Member member, Model model, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/updateForm";
		}

		postService.updatePost(id, postForm);
		redirectAttributes.addFlashAttribute("message", "게시글이 정상적으로 수정되었습니다.");
		return "redirect:/post/" + id + "/edit";
	}

}
