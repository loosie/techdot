package com.techdot.techdot.modules.interest;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.interest.dto.InterestCategoryResponseDto;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service @Slf4j
@RequiredArgsConstructor
public class InterestService {

	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final InterestRepository interestRepository;

	/**
	 * 멤버 관심 카테고리 등록하기
	 * @param memberId
	 * @param categoryName
	 */
	public void add(final Long memberId, final String categoryName) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId);
		Category findCategory = categoryRepository.getByViewName(categoryName);

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

	/**
	 * 멤버 관심 카테고리 제거하기
	 * @param memberId
	 * @param categoryName
	 */
	public void remove(final Long memberId, final String categoryName) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId);
		Category findCategory = categoryRepository.getByViewName(categoryName);

		Optional<Interest> interest = interestRepository.findByMemberAndCategory(findMember, findCategory);
		if(interest.isEmpty()){
			throw new RuntimeException("관심 카테고리 정보가 올바르지 않습니다. 다시 시도해주세요.");
		}

		interestRepository.delete(interest.get());
	}

	public List<InterestCategoryResponseDto>  getInterestCategoriesByMember(final Long memberId) {
		List<InterestCategoryResponseDto> allCategories = interestRepository.findAllCategoriesByMemberId(
			memberId);
		return allCategories;
	}
}
