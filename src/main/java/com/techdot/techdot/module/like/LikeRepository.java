package com.techdot.techdot.module.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.post.Post;

@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByMemberAndPost(Member me, Post post);
}
