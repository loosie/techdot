package com.techdot.techdot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByName(CategoryName name);
}
