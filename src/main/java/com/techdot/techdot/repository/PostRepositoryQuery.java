package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.dto.PostQueryDto;

@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostQueryDto> findQueryDtoByCategoryName_ifMember_withIsMemberLike(Long memberId, String categoryName, Pageable pageable);

	List<PostQueryDto> findQueryDtoByLikesMemberId(Long memberId, Pageable pageable);

	List<PostQueryDto> findQueryDtoWithIsMemberLikeByInterestsMemberId(Long memberId, Pageable pageable);
}
