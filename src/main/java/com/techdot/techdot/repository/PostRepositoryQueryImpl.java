package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.dto.PostQueryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl  implements PostRepositoryQuery{

	private final EntityManager em;

	@Override
	public List<PostQueryDto> findAllByDto(Pageable pageable) {
		return em.createQuery(
			"select new com.techdot.techdot.dto.PostQueryDto(p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage)" +
				" from Post p", PostQueryDto.class)
			.setFirstResult(pageable.getPageSize()*(pageable.getPageNumber()-1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

}
