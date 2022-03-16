package com.techdot.techdot.modules.post;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryExtension{
	boolean existsByLink(String link);

	Page<Post> findByManager(Member manager, Pageable pageable);


}
