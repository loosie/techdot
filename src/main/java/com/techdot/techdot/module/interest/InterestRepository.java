package com.techdot.techdot.module.interest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.module.category.Category;
import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.interest.dto.InterestCategoryResponseDto;

@Transactional(readOnly = true)
public interface InterestRepository extends JpaRepository<Interest, Long> {

	Optional<Interest> findByMemberAndCategory(Member member, Category category);

	@Query("select new com.techdot.techdot.module.interest.dto.InterestCategoryResponseDto(c.name) from Interest i join i.category c where i.member.id = :memberId")
	List<InterestCategoryResponseDto> findAllCategoriesByMemberId(@Param("memberId") Long memberId);
}
