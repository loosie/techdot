package com.techdot.techdot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Post;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {
	boolean existsByLink(String link);
}