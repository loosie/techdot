package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.dto.PostQueryDto;

@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostQueryDto> findByCategoryName(String categoryName, Pageable pageable);

	List<PostQueryDto> findWithIsMemberLikeByCategoryName(Long memberId, String categoryName, Pageable pageable);

	List<PostQueryDto> findByLikesMemberId(Long memberId, Pageable pageable);

	List<PostQueryDto> findWithIsMemberLikeByInterestsMemberId(Long memberId, Pageable pageable);
}
