package com.techdot.techdot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Interest;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.dto.InterestCategoryResponseDto;

@Transactional(readOnly = true)
public interface InterestRepository extends JpaRepository<Interest, Long> {

	Optional<Interest> findByMemberAndCategory(Member member, Category category);

	@Query("select new com.techdot.techdot.dto.InterestCategoryResponseDto(c.name) from Interest i join i.category c where i.member.id = :memberId")
	List<InterestCategoryResponseDto> findAllWithCategoryByMember(@Param("memberId") Long memberId);
}
