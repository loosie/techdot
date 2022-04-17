package com.techdot.techdot.modules.like;

import org.springframework.stereotype.Service;

import com.sun.jdi.request.DuplicateRequestException;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.Post;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final LikeRepository likeRepository;

	/**
	 * 좋아요 추가하기
	 * @param memberId
	 * @param postId
	 * @throws DuplicateRequestException 이미 등록된 좋아요 정보가 있을 경우 예외 발생
	 */
	public void add(final Long memberId, final Long postId) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId); // 이미 인증된 객체
		Post findPost = postRepository.findById(postId).orElseThrow(NullPointerException::new);

		if(likeRepository.findByMemberAndPost(findMember, findPost).isPresent()){
			throw new DuplicateRequestException("이미 좋아요를 누른 게시글입니다.");
		}

		// 좋아요 생성
		Like like = Like.builder()
			.member(findMember)
			.post(findPost)
			.build();

		likeRepository.save(like);
	}

	/**
	 * 좋아요 삭제하기
	 * @param memberId
	 * @param postId
	 * @throws NullPointerException 등록된 좋아요 정보가 없을 경우 예외 발생
	 */
	public void remove(final Long memberId, final Long postId) {
		// 엔티티 조회
		Member findMember = memberRepository.getById(memberId); // 이미 인증된 객체
		Post findPost = postRepository.findById(postId).orElseThrow(NullPointerException::new);

		Like like = likeRepository.findByMemberAndPost(findMember, findPost).orElseThrow(() ->
			new NullPointerException("게시글의 정보가 올바르지 않습니다. 다시 시도해주세요."));

		likeRepository.delete(like);
	}
}
