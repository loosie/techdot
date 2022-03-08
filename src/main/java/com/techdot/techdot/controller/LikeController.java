package com.techdot.techdot.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.LikeFormDto;
import com.techdot.techdot.service.LikeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping("/like/add")
	public ResponseEntity likeAdd(@CurrentUser Member member, @RequestBody LikeFormDto likeForm) {
		likeService.add(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/like/remove")
	public ResponseEntity likeRemove(@CurrentUser Member member, @RequestBody LikeFormDto likeForm) {
		System.out.println(member);
		System.out.println(likeForm.toString());

		likeService.remove(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

}
