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

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.repository.PostRepository;
import com.techdot.techdot.repository.PostRepositoryQueryImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final PostRepositoryQueryImpl postRepositoryQuery;
	private final PostRepository postRepository;

	@GetMapping("/")
	public String home(@CurrentUser Member member, Model model){
		// model.addAttribute("postList", postRepository.findAll());
		if(member != null){
			model.addAttribute(member);
		}
		return "index";
	}

	@GetMapping("/post/scrollList")
	public ResponseEntity<List<PostQueryDto>> postScroll(@PageableDefault(page=0, size=12, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
		List<PostQueryDto> allByDto = postRepositoryQuery.findAllByDto(pageable);
		return new ResponseEntity<>(allByDto, HttpStatus.OK);
	}

	@GetMapping("/login")
	public String login(){
		return "login";
	}
}
