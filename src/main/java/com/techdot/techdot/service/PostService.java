package com.techdot.techdot.service;

import java.util.List;
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
		// 엔티티 조회
		Member manager = memberRepo.findById(member.getId()).orElseThrow(NullPointerException::new);

		// 게시글 생성
		Post newPost = Post.builder()
			.title(postForm.getTitle())
			.link(postForm.getLink())
			.thumbnailImage(postForm.getThumbnailImage())
			.content(postForm.getContent())
			.type(postForm.getType())
			.writer(postForm.getWriter())
			.manager(manager) // getPost() -> select 1번 (모든 게시글 불러옴)
			.build();

		// 게시글 저장
		postRepo.save(newPost); // insert 1번
	}

	public List<Post> getMemberPosts(String nickname) {
		// 엔티티 조회
		Member manager = memberRepo.findByNickname(nickname).orElseThrow(NullPointerException::new);

		// 게시글 전체 조회
		return manager.getPosts();
	}

	public Post getPostById(Long id) {
		return postRepo.getById(id);
	}

	public void updatePost(Long postId, PostFormDto postForm) {
		Post post = postRepo.findById(postId).orElseThrow(NullPointerException::new);
		post.update(postForm);
		postRepo.save(post);
	}
}
