package com.techdot.techdot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.techdot.techdot.dto.PostQueryDto;

public interface PostRepositoryExtension {

	Page<PostQueryDto> findByKeyword(String keyword, Pageable pageable);

	Page<PostQueryDto> findWithIsMemberLikeByKeyword(Long memberId, String keyword, Pageable pageable);

}
