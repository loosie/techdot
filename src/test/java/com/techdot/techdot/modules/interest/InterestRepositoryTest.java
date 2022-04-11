package com.techdot.techdot.modules.interest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.interest.dto.InterestCategoryResponseDto;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;

@TCDataJpaTest
class InterestRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private InterestRepository interestRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	private Member member;

	@BeforeEach
	void setUp() {
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		memberRepository.save(member);
		categoryRepository.save(Category.builder().viewName("java").title("자바").name("Java").build());
	}

	@DisplayName("관심 생성하기 - 성공")
	@Test
	void like_create_success() {
		Category category = categoryRepository.getByViewName("java");
		Interest interest = Interest.builder()
			.member(member)
			.category(category)
			.build();
		Interest saveInterest = interestRepository.save(interest);

		// then
		assertEquals(saveInterest.getCategory(), category);
		assertEquals(saveInterest.getMember(), member);
	}

	@DisplayName("멤버와 카테고리로 관심 조회하기")
	@Test
	void interest_findByMemberAndCategory() {
		Category category = categoryRepository.getByViewName("java");
		Interest interest = Interest.builder()
			.member(member)
			.category(category)
			.build();
		interestRepository.save(interest);

		// when
		Interest findInterest = interestRepository.findByMemberAndCategory(member, category).get();

		// then
		assertNotNull(findInterest);
		assertEquals(findInterest.getMember(), member);
		assertEquals(findInterest.getCategory(), category);
	}

	@DisplayName("멤버 ID로 관심 카테고리 모두 조회하기")
	@Test
	void findAllInterestCategories_byMemberId() {
		List<Category> allCategories = categoryRepository.findAll();
		for (Category category : allCategories) {
			Interest interest = Interest.builder()
				.member(member)
				.category(category)
				.build();
			interestRepository.save(interest);
		}
		List<InterestCategoryResponseDto> allCategoriesByMemberId = interestRepository.findAllCategoriesByMemberId(
			member.getId());

		assertEquals(allCategoriesByMemberId.size(), allCategories.size());
	}

	@DisplayName("멤버 id로 해당 멤버 관심 카테고리 모두 삭제하기")
	@Test
	void interests_allDelete_byMemberId() {
		// given
		Category category = categoryRepository.getByViewName("java");
		Interest interest = Interest.builder()
			.member(member)
			.category(category)
			.build();
		interestRepository.save(interest);

		// when
		assertEquals(interestRepository.findAllCategoriesByMemberId(member.getId()).size(), 1);
		interestRepository.deleteAllByMemberId(member.getId());

		// then
		assertEquals(interestRepository.findAllCategoriesByMemberId(member.getId()).size(), 0);
	}
}