package com.techdot.techdot.modules.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findByName(String name);

	boolean existsByName(String name);
	boolean existsByTitle(String title);
	boolean existsByViewName(String viewName);
}
