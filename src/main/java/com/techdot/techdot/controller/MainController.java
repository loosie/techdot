package com.techdot.techdot.controller;

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

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.repository.LikeRepository;
import com.techdot.techdot.repository.PostRepository;
import com.techdot.techdot.repository.PostRepositoryQueryImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final PostRepositoryQueryImpl postRepositoryQuery;

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model) {
		if (member != null) {
			model.addAttribute(member);
		}
		return "index";
	}

	@GetMapping("/category/{categoryName}")
	public String home_cs(@PathVariable String categoryName, @CurrentUser Member member, Model model) {
		if (member != null) {
			model.addAttribute(member);
		}
		return CategoryName.valueOf(categoryName).getViewName();
	}

	@GetMapping("/posts/{categoryName}")
	public ResponseEntity<List<PostQueryDto>> postScrollByCategoryName(@PathVariable String categoryName,
		@PageableDefault(page = 0, size = 12, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		if (categoryName.equals("All")) {
			return new ResponseEntity<>(postRepositoryQuery.findAllDto(pageable), HttpStatus.OK);
		}

		List<PostQueryDto> posts = postRepositoryQuery.findDtoByCategoryName(CategoryName.valueOf(categoryName),
			pageable);
		return new ResponseEntity<>(posts, HttpStatus.OK);
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
