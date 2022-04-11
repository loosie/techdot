package com.techdot.techdot.modules.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.Post;

@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByMemberAndPost(Member member, Post post);

	@Transactional
	void deleteAllByMemberId(Long memberId);
}
