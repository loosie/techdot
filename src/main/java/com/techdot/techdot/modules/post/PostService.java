package com.techdot.techdot.modules.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;

	// 쿼리 발생 횟수 : 4
	// validator url 중복 조회 쿼리
	// manger 조회 쿼리
	// category 조회 쿼리
	// post insert 쿼리
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

	public Page<Post> getByManager(Member member, Pageable pageable) {
		return postRepository.findByManager(member, pageable);
	}

	// 카테고리별로 게시글 가져오기 (만약 멤버가 좋아하면 멤버가 좋아요 누른 게시글 정보도 가져오기)
	// post 조회(if(Member) 좋아요 여부) 쿼리 1번
	public List<PostQueryResponseDto> getPostsByCategoryNameIfMemberWithMemberLikes(Member member, String categoryName,
		Pageable pageable) {
		Long memberId = -1L;
		if(member != null){
			memberId = member.getId();
		}

		if (categoryName.equals("All")) {
			return postRepository.findAllDto(memberId, pageable);
		}
		return postRepository.findAllDtoByCategoryName(memberId, CategoryName.valueOf(categoryName), pageable);
	}

	// 멤버가 좋아요 누른 게시글 가져오기
	public List<PostQueryResponseDto> getPostsByLikesMemberId(Long memberId, Pageable pageable) {
		List<PostQueryResponseDto> allLikePosts = postRepository.findAllDtoByLikesMemberId(memberId, pageable);
		return allLikePosts;
	}

	// 멤버의 관심 카테고리 게시글 가져오기
	public List<PostQueryResponseDto> getPostsByInterestsMemberId(Long memberId, Pageable pageable) {
		List<PostQueryResponseDto> allInterestPosts = postRepository.findAllDtoByInterestsMemberId(memberId, pageable);
		return allInterestPosts;
	}

	// keyword 검색
	public List<PostQueryResponseDto> getPostsByKeyword(Member member, String keyword, Pageable pageable) {
		Long memberId = -1L;
		if(member != null){
			memberId = member.getId();
		}
		return postRepository.findAllDtoByKeyword(memberId, keyword, pageable);
	}
}
