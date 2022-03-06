package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.dto.PostQueryDto;


@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostQueryDto> findAllDto(Pageable pageable);

	List<PostQueryDto> findAllDtoByCategoryName(CategoryName category, Pageable pageable);
}
