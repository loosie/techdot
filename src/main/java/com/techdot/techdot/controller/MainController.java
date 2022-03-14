package com.techdot.techdot.controller;

import static com.techdot.techdot.domain.CategoryName.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.repository.PostRepository;
import com.techdot.techdot.service.PostService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final PostRepository postRepository;
	private final PostService postService;

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model) {
		if (member != null) {
			if (!member.getEmailVerified()) {
				model.addAttribute("email", member.getEmail());
				return "redirect:/check-email";
			}
			model.addAttribute(member);
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/category/{categoryName}")
	public String homeByCategory(@PathVariable String categoryName, @CurrentUser Member member, Model model,
		@PageableDefault(size = 10, page = 0, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		if (member != null) {
			model.addAttribute(member);
		}
		model.addAttribute("sortProperty",
			pageable.getSort().toString().contains("uploadDateTime") ? "uploadDateTime" : "id");
		return getMainViewName(categoryName);
	}

	@GetMapping("/me/interests")
	public String MyInterestsView(@CurrentUser Member member, Model model) {
		if (!member.getEmailVerified()) {
			model.addAttribute("email", member.getEmail());
			return "redirect:/check-email";
		}
		model.addAttribute(member);
		return "main/my-interests";
	}

	@GetMapping("/search")
	public String search(@CurrentUser Member member, String keyword, Model model) {
		if(member != null){
			model.addAttribute(member);
		}
		model.addAttribute("keyword", keyword);
		return "search";
	}

	@GetMapping("/search/{keyword}")
	public ResponseEntity<List<PostQueryDto>> searchPostsByKeyword_scrolling(
		@PathVariable String keyword, @CurrentUser Member member,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		List<PostQueryDto> result = postService.getPostsByKeyword(member, keyword, pageable);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/error/{status}")
	public String errorView(@PathVariable String status) {
		return "error/" + status;
	}
}
