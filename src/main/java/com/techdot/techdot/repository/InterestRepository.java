package com.techdot.techdot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Interest;
import com.techdot.techdot.domain.Member;

@Transactional(readOnly = true)
public interface InterestRepository extends JpaRepository<Interest, Long> {

	Optional<Interest> findByMemberAndCategory(Member member, Category category);
}
