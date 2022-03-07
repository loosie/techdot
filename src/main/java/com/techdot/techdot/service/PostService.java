package com.techdot.techdot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PostFormDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepo;
	private final MemberRepository memberRepo;
	private final CategoryRepository categoryRepository;

	// 쿼리 발생 횟수
	// validator url 중복 조회
	// manger 조회
	// category 조회
	// post insert
	public void post(PostFormDto postForm, Member member) {
		// 엔티티 조회
		Member manager = memberRepo.findById(member.getId()).orElseThrow(NullPointerException::new);
		Category category = categoryRepository.findByName(postForm.getCategoryName()).orElseThrow(NullPointerException::new);

		// 게시글 생성
		Post newPost = Post.builder()
			.title(postForm.getTitle())
			.link(postForm.getLink())
			.thumbnailImage(postForm.getThumbnailImage())
			.content(postForm.getContent())
			.type(postForm.getType())
			.writer(postForm.getWriter())
			.manager(manager)
			.category(category)
			.build();

		// 게시글 저장
		postRepo.save(newPost);
	}


	public Post getPostById(Long id) {
		return postRepo.getById(id);
	}

	public void updatePost(Long postId, PostFormDto postForm) {
		Post post = postRepo.findById(postId).orElseThrow(NullPointerException::new);
		post.update(postForm);
		postRepo.save(post);
	}

	public Page<Post> findByManager(Member member, Pageable pageable) {
		return postRepo.findByManager(member, pageable);
	}

}
