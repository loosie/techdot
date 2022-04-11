package com.techdot.techdot.modules.interest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.interest.dto.InterestCategoryResponseDto;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;

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
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		category = Category.builder()
			.viewName("java")
			.title("JAVA")
			.name("자바")
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
		given(memberRepository.getById(1L)).willReturn(member);
		given(categoryRepository.getByViewName("java")).willReturn(category);
		given(interestRepository.findByMemberAndCategory(member, category)).willReturn(Optional.empty());

		// when
		interestService.add(1L, "java");

		// then
		then(memberRepository).should(times(1)).getById(any());
		then(categoryRepository).should(times(1)).getByViewName(any());
		then(interestRepository).should(times(1)).findByMemberAndCategory(any(), any());
		then(interestRepository).should(times(1)).save(Interest.builder().member(member).category(category).build());
	}

	@DisplayName("관심 카테고리 삭제하기")
	@Test
	void interestRemove() {
		// given
		Interest interest = Interest.builder().member(member).category(category).build();
		given(memberRepository.getById(1L)).willReturn(member);
		given(categoryRepository.getByViewName("java")).willReturn(category);
		given(interestRepository.findByMemberAndCategory(member, category)).willReturn(Optional.of(interest));

		// when
		interestService.remove(1L, "java");

		// then
		then(memberRepository).should(times(1)).getById(any());
		then(categoryRepository).should(times(1)).getByViewName(any());
		then(interestRepository).should(times(1)).findByMemberAndCategory(any(), any());
		then(interestRepository).should(times(1)).delete(interest);
	}

	@DisplayName("멤버 ID로 멤버 관심 카테고리 목록 가져오기")
	@Test
	void getInterestCategories_byMemberId(){
		List<InterestCategoryResponseDto> input = List.of(
			new InterestCategoryResponseDto("java"), new InterestCategoryResponseDto("backend"));
		given(interestRepository.findAllCategoriesByMemberId(member.getId())).willReturn(input);
		List<InterestCategoryResponseDto> result = interestService.getInterestCategoriesByMember(member.getId());

		then(interestRepository).should(times(1)).findAllCategoriesByMemberId(any());
		assertEquals(input.get(0), result.get(0));
	}
}