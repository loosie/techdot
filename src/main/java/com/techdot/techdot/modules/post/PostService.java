package com.techdot.techdot.modules.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.modules.category.Category;
import com.techdot.techdot.modules.category.CategoryName;
import com.techdot.techdot.modules.category.CategoryRepository;
import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberRepository;
import com.techdot.techdot.modules.post.dto.PostFormDto;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;

	/**
	 * 게시글 업로드
	 * 쿼리 발생 횟수 : 4
	 *  validator url 중복 조회 쿼리 + manger 조회 쿼리 + category 조회 쿼리 + 게시글 insert 쿼리
	 * @param postForm
	 * @param memberId
	 */
	public void save(final PostFormDto postForm, final Long memberId) {
		// 엔티티 조회
		Member manager = memberRepository.findById(memberId).get(); // 이미 인증된 객체
		Category category = categoryRepository.findByName(postForm.getCategoryName());

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

	/**
	 * 게시글 업데이트 하기
	 * title, content, type, link, writer, thumbnailImage, uploadDateTime
	 * @param postId
	 * @param postForm
	 */
	public void updatePost(final Long postId, final PostFormDto postForm) {
		Post post = postRepository.findById(postId).orElseThrow(NullPointerException::new);
		post.update(postForm);
		postRepository.save(post);
	}

	/**
	 * 게시글 id로 조회하기
	 * 쿼리 발생 횟수 : 1 - 게시글 조회 쿼리
	 * @param id
	 * @return
	 */
	public Post getPostById(final Long id) {
		return postRepository.getById(id);
	}

	/**
	 * 게시글 Manager로 조회하기
	 * @param member
	 * @param pageable
	 * @return
	 */
	public Page<Post> getByManager(final Member member, final Pageable pageable) {
		return postRepository.findByManager(member, pageable);
	}

	/**
	 * 카테고리별로 게시글 가져오기
	 * @param member
	 * @param categoryName
	 * @param pageable
	 * @return
	 */
	public List<PostQueryResponseDto> getPostsByCategoryNameIfMemberWithMemberLikes(final Member member,
		final String categoryName,
		final Pageable pageable) {
		Long memberId = -1L;
		if (member != null) {
			memberId = member.getId();
		}

		if (categoryName.equals("All")) {
			return postRepository.findAllDto(memberId, pageable);
		}
		return postRepository.findAllDtoByCategoryName(memberId, CategoryName.valueOf(categoryName), pageable);
	}

	/**
	 * 멤버가 좋아요 누른 게시글 가져오기
	 * @param memberId
	 * @param pageable
	 * @return
	 */
	public List<PostQueryResponseDto> getPostsByLikesMemberId(final Long memberId, final Pageable pageable) {
		List<PostQueryResponseDto> allLikePosts = postRepository.findAllDtoByLikesMemberId(memberId, pageable);
		return allLikePosts;
	}

	/**
	 * 멤버의 관심 카테고리 게시글 가져오기
	 * @param memberId
	 * @param pageable
	 * @return
	 */
	public List<PostQueryResponseDto> getPostsByInterestsMemberId(final Long memberId, final Pageable pageable) {
		List<PostQueryResponseDto> allInterestPosts = postRepository.findAllDtoByInterestsMemberId(memberId, pageable);
		return allInterestPosts;
	}

	/**
	 * {keyword}로 게시글 검색하기
	 * @param member
	 * @param keyword
	 * @param pageable
	 * @return
	 */
	public List<PostQueryResponseDto> getPostsByKeyword(final Member member, String keyword, final Pageable pageable) {
		Long memberId = -1L;
		if (member != null) {
			memberId = member.getId();
		}
		return postRepository.findAllDtoByKeyword(memberId, keyword, pageable);
	}
}
