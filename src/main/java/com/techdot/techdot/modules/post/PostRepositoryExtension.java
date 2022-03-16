package com.techdot.techdot.modules.post;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;

public interface PostRepositoryExtension {

	List<PostQueryResponseDto> findAllDto(Long memberId, Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByKeyword(Long memberId, String keyword, Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByCategoryName(Long memberId, CategoryName categoryName, Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByLikesMemberId(Long memberId, Pageable pageable);

	List<PostQueryResponseDto> findAllDtoByInterestsMemberId(Long memberId, Pageable pageable);
}
