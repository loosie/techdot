package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.dto.PostQueryDto;


@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostQueryDto> findWithCategoryByCategoryName(String categoryName, Pageable pageable);

	List<Long> findIdWithLikesAndCategoryByMember(Long memberId, String categoryName);

	List<PostQueryDto> findWithCategoryAndLikesByMember(Long memberId, Pageable pageable);
}
