package com.techdot.techdot.modules.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryExtension {
	boolean existsByLink(final String link);

	@Query("select new com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto(p.id, p.title, p.writer, p.uploadDateTime, p.category.viewName) from Post p join p.category c where p.manager = :manager")
	Page<MyUploadPostResponseDto> getByManager(final Member manager, final Pageable pageable);

}
