package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.dto.PostQueryDto;


@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostQueryDto> findQueryDtoByCategoryName(String categoryName, Pageable pageable);

	List<Long> findIdByLikesMemberId(Long memberId, String categoryName);

	List<PostQueryDto> findQueryDtoByLikesMemberId(Long memberId, Pageable pageable);

	List<PostQueryDto> findQueryDtoByInterestsMemberId(Long memberId, Pageable pageable);
}
