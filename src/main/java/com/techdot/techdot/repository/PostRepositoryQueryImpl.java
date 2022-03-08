package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Like;
import com.techdot.techdot.domain.PostType;
import com.techdot.techdot.dto.PostQueryDto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

	private final EntityManager em;

	@Override
	public List<PostQueryDto> findAllDto(Pageable pageable) {
		return em.createQuery(
			"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name)"
				+
				" from Post p" +
				" join p.category c"
			, PostQueryDto.class)
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

	public List<PostQueryDto> findDtoByCategoryName(CategoryName categoryName, Pageable pageable) {
		return em.createQuery(
			"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name)"
				+
				" from Post p" +
				" join p.category c" +
				" where p.category.name = :categoryName"
			, PostQueryDto.class)
			.setParameter("categoryName", categoryName)
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}


}
