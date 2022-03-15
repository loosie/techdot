package com.techdot.techdot.module.main;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.member.auth.CurrentUser;
import com.techdot.techdot.module.post.PostService;
import com.techdot.techdot.module.post.dto.PostQueryResponseDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

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

	@GetMapping("/me/interests")
	public String MyInterestsView(@CurrentUser Member member, Model model) {
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
	public ResponseEntity<List<PostQueryResponseDto>> searchPostsByKeyword_scrolling(
		@PathVariable String keyword, @CurrentUser Member member,
		@PageableDefault(page = 0, size = 12, sort = "uploadDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
		List<PostQueryResponseDto> result = postService.getPostsByKeyword(member, keyword, pageable);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/error/{status}")
	public String errorView(@PathVariable String status) {
		return "error/" + status;
	}
}
