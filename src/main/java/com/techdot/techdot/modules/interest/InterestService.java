package com.techdot.techdot.modules.interest;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sun.jdi.request.DuplicateRequestException;
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
	 * @param categoryViewName
	 * @throws DuplicateRequestException 이미 등록된 관심 카테고리일 경우 예외 발생
	 */
	public void add(final Long memberId, final String categoryViewName) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId);
		Category findCategory = categoryRepository.getByViewName(categoryViewName);

		if(interestRepository.findByMemberAndCategory(findMember, findCategory).isPresent()){
			throw new DuplicateRequestException("이미 등록된 관심 카테고리입니다.");
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
	 * @param categoryViewName
	 * @throws NullPointerException 등록된 관심 카테고리가 아닐 경우 예외 발생
	 */
	public void remove(final Long memberId, final String categoryViewName) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId);
		Category findCategory = categoryRepository.getByViewName(categoryViewName);

		Interest interest = interestRepository.findByMemberAndCategory(findMember, findCategory)
			.orElseThrow(() -> new NullPointerException("등록된 관심 카테고리가 아닙니다."));

		interestRepository.delete(interest);
	}

	/**
	 * 멤버의 관심 카테고리 목록 가져오기
	 * @param memberId
	 */
	public List<InterestCategoryResponseDto>  getInterestCategoriesByMember(final Long memberId) {
		return interestRepository.findAllCategoriesByMemberId(memberId);
	}
}
