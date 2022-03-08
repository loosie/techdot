package com.techdot.techdot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;

@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {
	List<Like> findAllByMember(Member member);

	boolean existsByMember(Member member);

	Optional<Like> findByMemberAndPost(Member me, Post post);
}