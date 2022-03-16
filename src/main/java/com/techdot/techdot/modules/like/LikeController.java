package com.techdot.techdot.modules.like;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.like.dto.LikeFormDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping("/like/add")
	public ResponseEntity likeAdd(@CurrentUser Member member, @RequestBody LikeFormDto likeForm, Errors errors) {
		likeService.add(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/like/remove")
	public ResponseEntity likeRemove(@CurrentUser Member member, @RequestBody LikeFormDto likeForm) {
		likeService.remove(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

}
