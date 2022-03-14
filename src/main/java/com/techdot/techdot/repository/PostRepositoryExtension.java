package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.dto.PostQueryDto;

public interface PostRepositoryExtension {

	List<PostQueryDto> findAllDto(Long memberId, Pageable pageable);

	List<PostQueryDto> findAllDtoByKeyword(Long memberId, String keyword, Pageable pageable);

	List<PostQueryDto> findAllDtoByCategoryName(Long memberId, CategoryName categoryName, Pageable pageable);

	List<PostQueryDto> findAllDtoByLikesMemberId(Long memberId, Pageable pageable);

	List<PostQueryDto> findAllDtoByInterestsMemberId(Long memberId, Pageable pageable);
}
