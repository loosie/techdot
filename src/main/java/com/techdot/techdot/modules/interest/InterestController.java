package com.techdot.techdot.modules.interest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.techdot.techdot.modules.member.auth.CurrentUser;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.interest.dto.InterestCategoryResponseDto;
import com.techdot.techdot.modules.interest.dto.InterestFormDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InterestController {

	private final InterestService interestService;

	/**
	 * 관심 카테고리 추가 API
	 * 쿼리 발생 횟수 : 4
	 * 멤버 조회 + 카테고리 조회 + 관심 조회 + 관심 추가
	 * @param member
	 * @param interestForm
	 * @return
	 */
	@PostMapping("/interest/add")
	public ResponseEntity likeAdd(@CurrentUser Member member, @RequestBody InterestFormDto interestForm) {
		interestService.add(member.getId(), interestForm.getCategoryName());
		return ResponseEntity.ok().build();
	}

	/**
	 * 관심 카테고리 삭제 API
	 * 쿼리 발생 횟수 : 4
	 * 멤버 조회 + 카테고리 조회 + 관심 조회 + 관심 삭제
	 * @param member
	 * @param interestForm
	 * @return
	 */
	@PostMapping("/interest/remove")
	public ResponseEntity likeRemove(@CurrentUser Member member, @RequestBody InterestFormDto interestForm) {
		interestService.remove(member.getId(), interestForm.getCategoryName());
		return ResponseEntity.ok().build();
	}

	/**
	 * 멤버 관심 카테고리 목록 조회 API
	 * @param member
	 * @return
	 */
	@GetMapping("/interests/me/list")
	public ResponseEntity<List<InterestCategoryResponseDto>> getInterestCategoriesByMember(@CurrentUser Member member) {
		return new ResponseEntity<>(interestService.getInterestCategoriesByMember(member.getId()), HttpStatus.OK);
	}


}
