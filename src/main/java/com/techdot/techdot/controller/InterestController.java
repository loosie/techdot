package com.techdot.techdot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.techdot.techdot.config.auth.CurrentUser;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.InterestCategoryResponseDto;
import com.techdot.techdot.dto.InterestFormDto;
import com.techdot.techdot.service.InterestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InterestController {

	private final InterestService interestService;

	@PostMapping("/interest/add")
	public ResponseEntity likeAdd(@CurrentUser Member member, @RequestBody InterestFormDto interestForm) {
		interestService.add(member.getId(), interestForm.getCategoryName());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/interest/remove")
	public ResponseEntity likeRemove(@CurrentUser Member member, @RequestBody InterestFormDto interestForm) {
		interestService.remove(member.getId(), interestForm.getCategoryName());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/interests/me/list")
	public ResponseEntity<List<InterestCategoryResponseDto>> getInterestCategoriesByMember(@CurrentUser Member member) {
		return new ResponseEntity<>(interestService.getInterestCategoriesByMember(member.getId()), HttpStatus.OK);
	}


}
