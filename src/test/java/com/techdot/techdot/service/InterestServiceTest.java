package com.techdot.techdot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Interest;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.InterestCategoryResponseDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.InterestRepository;
import com.techdot.techdot.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

	private InterestService interestService;
	@Mock private MemberRepository memberRepository;
	@Mock private CategoryRepository categoryRepository;
	@Mock private InterestRepository interestRepository;

	private static Member member;
	private static Category category;

	@BeforeAll
	static void init() {
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		category = Category.builder()
			.name(CategoryName.CS)
			.build();
	}

	@BeforeEach
	void setUp(){
		interestService = new InterestService(memberRepository, categoryRepository, interestRepository);
	}

	@DisplayName("관심 카테고리 추가하기")
	@Test
	void interestAdd() {
		// given
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(categoryRepository.findByName(CategoryName.CS)).willReturn(Optional.of(category));
		given(interestRepository.findByMemberAndCategory(member, category)).willReturn(Optional.empty());

		// when
		interestService.add(1L, "CS");

		// then
		then(memberRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findByName(any());
		then(interestRepository).should(times(1)).findByMemberAndCategory(any(), any());
		then(interestRepository).should(times(1)).save(Interest.builder().member(member).category(category).build());
	}

	@DisplayName("관심 카테고리 삭제하기")
	@Test
	void interestRemove() {
		// given
		Interest interest = Interest.builder().member(member).category(category).build();
		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(categoryRepository.findByName(CategoryName.CS)).willReturn(Optional.of(category));
		given(interestRepository.findByMemberAndCategory(member, category)).willReturn(Optional.of(interest));

		// when
		interestService.remove(1L, "CS");

		// then
		then(memberRepository).should(times(1)).findById(any());
		then(categoryRepository).should(times(1)).findByName(any());
		then(interestRepository).should(times(1)).findByMemberAndCategory(any(), any());
		then(interestRepository).should(times(1)).delete(interest);
	}

	@DisplayName("멤버 ID로 멤버 관심 카테고리 목록 가져오기")
	@Test
	void getInterestCategories_byMemberId(){
		List<InterestCategoryResponseDto> input = List.of(
			new InterestCategoryResponseDto(CategoryName.CS), new InterestCategoryResponseDto(CategoryName.Backend));
		given(interestRepository.findAllCategoriesByMemberId(member.getId())).willReturn(input);
		List<InterestCategoryResponseDto> result = interestService.getInterestCategoriesByMember(
			member);

		then(interestRepository).should(times(1)).findAllCategoriesByMemberId(any());
		assertEquals(input.get(0), result.get(0));
	}
}