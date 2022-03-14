package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.techdot.techdot.dto.PostQueryDto;

public interface PostRepositoryExtension {

	List<PostQueryDto> findByKeyword(String keyword, Pageable pageable);

	List<PostQueryDto> findWithIsMemberLikeByKeyword(Long memberId, String keyword, Pageable pageable);

}
