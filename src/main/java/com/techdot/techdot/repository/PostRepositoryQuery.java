package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.dto.PostCategoryQueryDto;


@Transactional(readOnly = true)
public interface PostRepositoryQuery {

	List<PostCategoryQueryDto> findAllDtoWithCategoryByCategoryName(String categoryName, Pageable pageable);

}
