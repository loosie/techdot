package com.techdot.techdot.modules.post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.post.image.PostS3Service;
import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.dto.MyUploadPostResponseDto;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.dto.PostImageFormDto;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;

	private final PostS3Service s3Service;

	/**
	 * 게시글 저장
	 */
	public Post save(final PostFormDto postForm, final Long memberId) {
		// 엔티티 조회
		Member manager = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Category category = categoryRepository.getByViewName(postForm.getCategoryName());

		// 게시글 생성
		Post newPost = Post.builder()
			.title(postForm.getTitle())
			.link(postForm.getLink())
			.content(postForm.getContent())
			.type(postForm.getType())
			.writer(postForm.getWriter())
			.manager(manager)
			.category(category)
			.uploadDateTime(postForm.getUploadDateTime())
			.build();

		// 게시글 저장
		return postRepository.save(newPost);
	}

	/**
	 * 게시글 업데이트 하기
	 * @throws NullPointerException 존재하지 않는 게시글 id이면 예외 발생
	 */
	public void update(final Long postId, final PostFormDto postForm) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글 입니다."));
		Category category = categoryRepository.getByViewName(postForm.getCategoryName());

		post.update(postForm, category);

		postRepository.save(post);
	}

	/**
	 * 게시글 id로 조회하기
	 * @param id
	 * @throws NullPointerException 존재하지 않는 게시글 id이면 예외 발생
	 */
	public Post getById(final Long id) {
		return postRepository.findById(id).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글 입니다."));
	}

	/**
	 * 게시글 Manager로 조회하기
	 */
	public Page<MyUploadPostResponseDto> getByManager(final Member member, final Pageable pageable) {
		return postRepository.getByManager(member, pageable);
	}

	/**
	 * 카테고리별로 게시글 가져오기
	 */
	public List<PostQueryResponseDto> getPostsByCategoryNameIfMemberWithMemberLikes(final Member member,
		final String categoryViewName,
		final Pageable pageable) {
		Long memberId = -1L;
		if (member != null) {
			memberId = member.getId();
		}

		if (categoryViewName.equals("all")) {
			List<PostQueryResponseDto> allPosts = postRepository.findAllDto(memberId, pageable);
			return allPosts;
		}
		return postRepository.findAllDtoByCategoryViewName(memberId, categoryViewName, pageable);
	}

	/**
	 * 멤버가 좋아요 누른 게시글 가져오기
	 */
	public List<PostQueryResponseDto> getPostsByLikesMemberId(final Long memberId, final Pageable pageable) {
		List<PostQueryResponseDto> allLikePosts = postRepository.findAllDtoByLikesMemberId(memberId, pageable);
		return allLikePosts;
	}

	/**
	 * 멤버의 관심 카테고리 게시글 가져오기
	 */
	public List<PostQueryResponseDto> getPostsByInterestsMemberId(final Long memberId, final Pageable pageable) {
		List<PostQueryResponseDto> allInterestPosts = postRepository.findAllDtoByInterestsMemberId(memberId, pageable);
		return allInterestPosts;
	}

	/**
	 * keyword에 포함하는 게시글 검색하기
	 * 검색 범위
	 * - 게시글 tite, content, writer
	 * - 카테고리 name, title, viewName
	 */
	public List<PostQueryResponseDto> getPostsByKeyword(final Member member, String keyword, final Pageable pageable) {
		Long memberId = -1L;
		if (member != null) {
			memberId = member.getId();
		}
		return postRepository.findAllDtoByKeyword(memberId, keyword, pageable);
	}

	/**
	 * id로 게시글 삭제하기
	 */
	public void remove(Long id) {
		Post post = postRepository.getById(id);
		String key = post.getThumbnailImageUrl();
		postRepository.deleteById(id);
		if(key != null) {
			s3Service.delete(key);
		}

		log.info(id +"번 게시글이 정상적으로 삭제되었습니다.");
	}

	/**
	 * thumbnail 이미지 url 저장하기
	 */
	public void saveImageUrl(Long postId, PostImageFormDto postImageFormDto) {
		Post post = postRepository.getById(postId);
		post.setImageUrl(postImageFormDto.getThumbnailImageUrl());
		postRepository.save(post);
	}

	/**
	 * id로 게시글 조회하기
	 */
	public Optional<Post> findById(Long id) {
		return postRepository.findById(id);
	}
}
