package com.techdot.techdot.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PostQueryDto;
import com.techdot.techdot.dto.PostFormDto;
import com.techdot.techdot.repository.CategoryRepository;
import com.techdot.techdot.repository.MemberRepository;
import com.techdot.techdot.repository.PostRepository;
import com.techdot.techdot.repository.PostRepositoryQueryImpl;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final PostRepositoryQueryImpl postRepositoryQuery;

	// 쿼리 발생 횟수
	// validator url 중복 조회
	// manger 조회
	// category 조회
	// post insert
	public void save(PostFormDto postForm, Long memberId) {
		// 엔티티 조회
		Member manager = memberRepository.findById(memberId).get(); // 이미 인증된 객체
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
		postRepository.save(newPost);
	}


	public Post getPostById(Long id) {
		return postRepository.getById(id);
	}

	public void updatePost(Long postId, PostFormDto postForm) {
		Post post = postRepository.findById(postId).orElseThrow(NullPointerException::new);
		post.update(postForm);
		postRepository.save(post);
	}

	public Page<Post> findByManager(Member member, Pageable pageable) {
		return postRepository.findByManager(member, pageable);
	}

	public List<PostQueryDto> getPostsByCategoryClassifiedIsMemberLike(Long memberId, String categoryName, Pageable pageable) {
		// Post ByCategory 조회
		List<PostQueryDto> allPosts = postRepositoryQuery.findWithCategory(categoryName, pageable);

		// Member가 Like한 Post 조회
		List<Long> likePosts = postRepositoryQuery.findIdWithLikesAndCategoryByMember(memberId, categoryName);

		// 	Like한 Post 업데이트
		for(int i=0; i<allPosts.size(); i++){
			PostQueryDto post = allPosts.get(i);
			if(likePosts.contains(post.getPostId())){
				post.setIsMemberLike(true);
			}
		}
		return allPosts;
	}

	public List<PostQueryDto> getMemberLikesPosts(Long memberId, Pageable pageable) {
		List<PostQueryDto> allLikePosts = postRepositoryQuery.findWithCategoryAndLikesByMember(memberId, pageable);
		allLikePosts.stream().forEach(post -> post.setIsMemberLike(true));
		return allLikePosts;
	}
}
