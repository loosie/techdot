package com.techdot.techdot.modules.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category getByViewName(String viewName);
	Optional<Category> findByViewName(final String viewName);

	boolean existsByName(final String name);
	boolean existsByTitle(final String title);
	boolean existsByViewName(final String viewName);


}
