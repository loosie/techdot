package com.techdot.techdot.modules.interest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryName;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.interest.dto.InterestCategoryResponseDto;
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
	void setUp(){
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

		memberRepository.save(member);
		for(CategoryName categoryName : CategoryName.values()){
			categoryRepository.save(Category.builder().name(categoryName).build());
		}
	}

	@DisplayName("관심 생성하기 - 성공")
	@Test
	void like_create_success(){
		Category category = categoryRepository.findByName(CategoryName.CS);
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
	void interest_findByMemberAndCategory(){
		Category category = categoryRepository.findByName(CategoryName.CS);
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
	void findAllInterestCategories_byMemberId(){
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
}