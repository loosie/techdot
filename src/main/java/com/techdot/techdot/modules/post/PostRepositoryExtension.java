package com.techdot.techdot.modules.post;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;

public interface PostRepositoryExtension {

	List<PostQueryResponseDto> findAllDto(final Long memberId, final Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByKeyword(final Long memberId, final String keyword, final Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByCategoryName(final Long memberId, final String categoryName, final Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByLikesMemberId(final Long memberId, final Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByInterestsMemberId(final Long memberId, final Pageable pageable);
}
