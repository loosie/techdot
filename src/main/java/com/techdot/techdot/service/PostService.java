package com.techdot.techdot.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PostFormDto;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepo;
	private final MemberRepository memberRepo;

	public void post(PostFormDto postForm, Member member) {
		// 멤버 조회 (영속성 상태 붙이기 위함) select 1 번
		Member writer = memberRepo.findById(member.getId()).orElseThrow(NullPointerException::new);

		// 게시글 생성
		Post newPost = Post.builder()
			.title(postForm.getTitle())
			.link(postForm.getLink())
			.thumbnailImage(postForm.getThumbnailImage())
			.content(postForm.getContent())
			.type(postForm.getType())
			.member(writer) // getPost() -> select 1번 (모든 게시글 불러옴)
			.build();

		// 게시글 저장
		postRepo.save(newPost); // insert 1번
	}
}
