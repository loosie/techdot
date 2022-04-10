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

	/**
	 * 좋아요 추가 API
	 * 쿼리 발생 횟수 : 3
	 * 멤버 조회 + 게시글 조회 + 좋아요 등록
	 */
	@PostMapping("/like/add")
	public ResponseEntity likeAdd(@CurrentUser final Member member, @RequestBody final LikeFormDto likeForm) {
		likeService.add(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

	/**
	 * 좋아요 삭제 API
	 * 쿼리 발생 횟수 : 3
	 * 멤버 조회 + 게시글 조회 + 좋아요 삭제 
	 */
	@PostMapping("/like/remove")
	public ResponseEntity likeRemove(@CurrentUser final Member member, @RequestBody final LikeFormDto likeForm) {
		likeService.remove(member.getId(), likeForm.getPostId());
		return ResponseEntity.ok().build();
	}

}
