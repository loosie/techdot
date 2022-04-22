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

import com.techdot.techdot.modules.category.CategoryService;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.post.validator.PostFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final CategoryService categoryService;
	private final PostFormValidator postFormValidator;

	@InitBinder("postForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(postFormValidator);
	}

	/**
	 * (ADMIN) 게시글 업로드 뷰
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/new-post")
	public String newPostView(@CurrentUser final Member member, final Model model) {
		model.addAttribute("member", member);
		model.addAttribute("postForm", new PostFormDto());
		model.addAttribute("categoryList", categoryService.getAll());

		return "post/form";
	}

	/**
	 * (ADMIN) 게시글 업로드 요청
	 * 쿼리 발생 횟수 : 4
	 * validator url 중복 조회 쿼리 + manger 조회 쿼리 + category 조회 쿼리 + 게시글 insert 쿼리
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/new-post")
	public String newPostForm(@Valid @ModelAttribute("postForm") final PostFormDto postForm, final Errors errors,
		@CurrentUser final Member member, final Model model) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/form";
		}

		postService.save(postForm, member.getId());
		return "redirect:/";
	}

	/**
	 * (ADMIN) 게시글 업데이트 뷰
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/post/{id}/edit")
	public String updatePostView(@PathVariable final Long id, @CurrentUser final Member member, final Model model) {
		Post post = postService.getById(id);
		if (!post.isManager(member)) {
			return "redirect:/";
		}
		model.addAttribute(member);
		model.addAttribute("postId", id);
		model.addAttribute("postForm", new PostFormDto(post));
		model.addAttribute("categoryList", categoryService.getAll());
		return "post/updateForm";
	}

	/**
	 * (ADMIN) 게시글 업데이트 요청
	 * 쿼리 발생 횟수 : 7
	 * 카테고리 조회 + 게시글 조회 + 게시글 업데이트
	 * (리다이렉션) 업데이트된 게시글 +  게시글 카테고리 조회 + 전체 카테고리 조회 + 멤버 인가 조회
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/post/{id}/edit")
	public String updatePostForm(@PathVariable Long id, @Valid @ModelAttribute("postForm") final PostFormDto postForm,
		final Errors errors, @CurrentUser final Member member, final Model model, final RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute(member);
			return "post/updateForm";
		}

		postService.update(id, postForm);
		redirectAttributes.addFlashAttribute("message", "게시글이 정상적으로 수정되었습니다.");
		return "redirect:/post/" + id + "/edit";
	}

	/**
	 * (ADMIN) {id}로 게시글 삭제 요청
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/post/{id}/remove")
	public String removePostForm(@PathVariable final Long id) {
		postService.remove(id);
		return "redirect:/accounts/my-upload";
	}

	/**
	 * 카테고리별 게시글 조회 API (로그인된 멤버일 경우, 멤버가 좋아요 누른 게시글인지 내부 쿼리로 조회)
	 * 쿼리 발생 횟수 : 1 - PostQueryResponseDto 조회 쿼리
	 */
	@GetMapping("/posts/{categoryViewName}")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByCategory_scrolling(@PathVariable final String categoryViewName,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) final Pageable pageable,
		@CurrentUser final Member member) {
		return new ResponseEntity<>(
			postService.getPostsByCategoryNameIfMemberWithMemberLikes(member, categoryViewName, pageable), HttpStatus.OK);
	}


	/**
	 * 멤버가 좋아요 누른 게시글 조회 API
	 * 쿼리 발생 횟수 : 1 - PostQueryResponseDto 조회 쿼리
	 */
	@GetMapping("/posts/me/likes")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByMemberLikes_scrolling(
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable,
		@CurrentUser Member member) {
		return new ResponseEntity<>(postService.getPostsByLikesMemberId(member.getId(), pageable), HttpStatus.OK);
	}

	/**
	 * 멤버 관심 카테고리에 속한 게시글 조회 API
	 * 쿼리 발생 횟수 : 1 - PostQueryResponseDto 조회 쿼리
	 */
	@GetMapping("/posts/me/interests")
	public ResponseEntity<List<PostQueryResponseDto>> getPostsByMemberInterests_scrolling(
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) final Pageable pageable,
		@CurrentUser final Member member) {
		return new ResponseEntity<>(postService.getPostsByInterestsMemberId(member.getId(), pageable), HttpStatus.OK);
	}

	/**
	 * keyword 검색 API
	 * 쿼리 발생 횟수 : 1 - PostQueryResponseDto 조회 쿼리
	 */
	@GetMapping("/search/{keyword}")
	public ResponseEntity<List<PostQueryResponseDto>> searchPostsByKeyword_scrolling(
		@PathVariable String keyword, @CurrentUser final Member member,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) final Pageable pageable) {
		List<PostQueryResponseDto> result = postService.getPostsByKeyword(member, keyword, pageable);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
