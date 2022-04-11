package com.techdot.techdot.modules.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto;

@Transactional(readOnly = true)
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category getByViewName(final String viewName);
	Optional<Category> findByViewName(final String viewName);

	boolean existsByName(final String name);
	boolean existsByTitle(final String title);
	boolean existsByViewName(final String viewName);

	@Query("select c.id from Category c join c.posts p where p.category.id = :id")
	List<Long> findPostsByCategoryId(final Long id);


}
