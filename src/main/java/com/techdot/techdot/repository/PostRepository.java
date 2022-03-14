package com.techdot.techdot.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryExtension{
	boolean existsByLink(String link);

	List<Post> findByManager(Member manager, Pageable pageable);


}
