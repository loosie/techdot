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
import com.techdot.techdot.repository.PostRepositoryQuery;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final InterestService interestService;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;
	private final PostRepositoryQuery postRepositoryQuery;

	// 쿼리 발생 횟수
	// validator url 중복 조회
	// manger 조회
	// category 조회
	// post insert
	public void save(PostFormDto postForm, Long memberId) {
		// 엔티티 조회
		Member manager = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Category category = categoryRepository.findByName(postForm.getCategoryName())
			.orElseThrow(NullPointerException::new);

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
			.uploadDateTime(postForm.getUploadDateTime())
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

	// 카테고리별로 게시글 가져오기 (만약 멤버가 좋아하면 멤버가 좋아요 누른 게시글 정보도 가져오기)
	// post 조회(if(Member) 좋아요 여부) 쿼리 1번
	public List<PostQueryDto> getPostsByCategory_andIfMember_memberLikes(Member member, String categoryName,
		Pageable pageable) {
		if (member != null) {
			return postRepositoryQuery.findWithIsMemberLikeByCategoryName(member.getId(), categoryName, pageable);
		}
		return postRepositoryQuery.findByCategoryName(categoryName, pageable);
	}

	// 멤버가 좋아요 누른 게시글 가져오기
	public List<PostQueryDto> getPostsByMemberLikes(Long memberId, Pageable pageable) {
		List<PostQueryDto> allLikePosts = postRepositoryQuery.findByLikesMemberId(memberId, pageable);
		allLikePosts.stream().forEach(post -> post.setIsMemberLike(true));
		return allLikePosts;
	}

	public List<PostQueryDto> getPostsByMemberInterests(Long memberId, Pageable pageable) {
		List<PostQueryDto> allInterestPosts = postRepositoryQuery.findWithIsMemberLikeByInterestsMemberId(memberId, pageable);
		return allInterestPosts;
	}

}
