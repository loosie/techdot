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

	@DisplayName("관심 목록 생성하기")
	@Test
	void interestCreate_Success() {
		// given
		Category category = categoryRepository.getByViewName("java");
		Interest interest = Interest.builder()
			.member(member)
			.category(category)
			.build();

		// when
		Interest save = interestRepository.save(interest);

		// then
		assertEquals(save.getCategory(), category);
		assertEquals(save.getMember(), member);
	}

	@DisplayName("회원 정보와 카테고리로 관심 목록 조회하기")
	@Test
	void interestFindByMemberAndCategory_Success() {
		// given
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

	@DisplayName("회원 ID로 관심 목록 전체 조회하기")
	@Test
	void interestFindAllByMemberId_Success() {
		// given
		List<Category> allCategories = categoryRepository.findAll();
		for (Category category : allCategories) {
			Interest interest = Interest.builder()
				.member(member)
				.category(category)
				.build();
			interestRepository.save(interest);
		}

		// when
		List<InterestCategoryResponseDto> allCategoriesByMemberId = interestRepository.findAllCategoriesByMemberId(
			member.getId());

		// then
		assertEquals(allCategoriesByMemberId.size(), allCategories.size());
	}

	@DisplayName("회원 ID로 해당 회원 관심 목록 모두 삭제하기")
	@Test
	void interestDeleteAllByMemberId_Success() {
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