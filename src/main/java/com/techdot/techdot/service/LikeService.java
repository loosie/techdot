package com.techdot.techdot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.repository.LikeRepository;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;

	public void add(Long memberId, Long postId) {
		// 엔티티 조회
		Member findMember = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Post findPost = postRepository.findById(postId).orElseThrow(NullPointerException::new);

		if(likeRepository.findByMemberAndPost(findMember, findPost).isPresent()){
			throw new RuntimeException("이미 좋아요를 누른 게시글입니다.");
		}

		// 좋아요 생성
		Like like = Like.builder()
			.member(findMember)
			.post(findPost)
			.build();

		likeRepository.save(like);
	}

	public void remove(Long memberId, Long postId) {
		// 엔티티 조회
		Member findMember = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Post findPost = postRepository.findById(postId).orElseThrow(NullPointerException::new);

		Optional<Like> like = likeRepository.findByMemberAndPost(findMember, findPost);
		if(like.isEmpty()){
			throw new RuntimeException("게시글의 정보가 올바르지 않습니다. 다시 시도해주세요.");
		}

		likeRepository.delete(like.get());
	}
}
