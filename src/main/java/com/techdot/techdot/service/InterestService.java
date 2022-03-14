package com.techdot.techdot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Interest;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.InterestCategoryResponseDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.InterestRepository;
import com.techdot.techdot.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @Slf4j
@RequiredArgsConstructor
public class InterestService {

	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final InterestRepository interestRepository;

	public void add(Long memberId, String categoryName) {
		// 엔티티 조회
		Member findMember = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Category findCategory = categoryRepository.findByName(CategoryName.valueOf(categoryName)).orElseThrow(NullPointerException::new);

		if(interestRepository.findByMemberAndCategory(findMember, findCategory).isPresent()){
			throw new RuntimeException("이미 관심 카테고리에 등록한 카테고리입니다.");
		}

		// 좋아요 생성
		Interest interest = Interest.builder()
			.member(findMember)
			.category(findCategory)
			.build();

		interestRepository.save(interest);
	}

	public void remove(Long memberId, String categoryName) {
		// 엔티티 조회
		Member findMember = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Category findCategory = categoryRepository.findByName(CategoryName.valueOf(categoryName)).orElseThrow(NullPointerException::new);

		Optional<Interest> interest = interestRepository.findByMemberAndCategory(findMember, findCategory);
		if(interest.isEmpty()){
			throw new RuntimeException("관심 카테고리 정보가 올바르지 않습니다. 다시 시도해주세요.");
		}

		interestRepository.delete(interest.get());
	}

	public List<InterestCategoryResponseDto>  getInterestCategoriesByMember(Long memberId) {
		List<InterestCategoryResponseDto> allCategories = interestRepository.findAllCategoriesByMemberId(
			memberId);
		return allCategories;
	}
}
