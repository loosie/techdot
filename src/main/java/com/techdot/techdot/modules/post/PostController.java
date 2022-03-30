package com.techdot.techdot.modules.post;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.post.validator.PostFormValidator;

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

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/new-post")
	public String newPostView(@CurrentUser Member member, Model model) {
		model.addAttribute("member", member);
		model.addAttribute("postForm", new PostFormDto());

		return "post/form";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/new-post")
	public String newPostForm(@Valid @ModelAttribute("postForm") PostFormDto postForm, Errors errors,
		@CurrentUser Member member, Model model) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/form";
		}

		postService.save(postForm, member.getId());
		return "redirect:/";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/post/{id}/edit")
	public String updatePostView(@PathVariable Long id, @CurrentUser Member member, Model model) {
		Post post = postService.getPostById(id);
		if (!post.isManager(member)) {
			return "redirect:/";
		}
		model.addAttribute(member);
		model.addAttribute("postId", id);
		model.addAttribute("postForm", new PostFormDto(post));
		return "post/updateForm";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
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

	@GetMapping("/posts/{categoryName}")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByCategory_scrolling(@PathVariable String categoryName,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable,
		@CurrentUser Member member) {
		return new ResponseEntity<>(
			postService.getPostsByCategoryNameIfMemberWithMemberLikes(member, categoryName, pageable), HttpStatus.OK);
	}

	@GetMapping("/posts/me/likes")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByMemberLikes_scrolling(
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable,
		@CurrentUser Member member) {
		return new ResponseEntity<>(postService.getPostsByLikesMemberId(member.getId(), pageable), HttpStatus.OK);
	}

	@GetMapping("/posts/me/interests")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByMemberInterests_scrolling(
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable,
		@CurrentUser Member member) {
		return new ResponseEntity<>(postService.getPostsByInterestsMemberId(member.getId(), pageable), HttpStatus.OK);
	}

	@GetMapping("/search/{keyword}")
	public ResponseEntity<List<PostQueryResponseDto>> searchPostsByKeyword_scrolling(
		@PathVariable String keyword, @CurrentUser Member member,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		List<PostQueryResponseDto> result = postService.getPostsByKeyword(member, keyword, pageable);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
