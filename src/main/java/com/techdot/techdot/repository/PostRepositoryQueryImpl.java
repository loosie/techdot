package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.dto.PostCategoryQueryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

	private final EntityManager em;

	public List<PostCategoryQueryDto> findAllDtoWithCategoryByCategoryName(String categoryName, Pageable pageable) {
		String sql = "select new com.techdot.techdot.dto.PostCategoryQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name)" +
			" from Post p" +
			" join p.category c";
		if(categoryName.equals("All")){
			return em.createQuery(sql, PostCategoryQueryDto.class)
				.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
				.setMaxResults(pageable.getPageSize())
				.getResultList();
		}

		sql += " where p.category.name = :categoryName";
		return em.createQuery(sql, PostCategoryQueryDto.class)
			.setParameter("categoryName", CategoryName.valueOf(categoryName))
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

	public List<Long> findAllWithLikesAndCategoryByMemberIdAndCategoryName(Long memberId, String categoryName) {
		String sql = "select p.id" +
			" from Post p" +
			" join p.category c" +
			" join p.likes l" +
			" where l.member.id = :memberId";
		if(categoryName.equals("All")){
			return em.createQuery(sql, Long.class)
				.setParameter("memberId", memberId)
				.getResultList();
		}

		sql += " and p.category.name = :categoryName";
		return em.createQuery(sql, Long.class)
			.setParameter("memberId", memberId)
			.setParameter("categoryName", CategoryName.valueOf(categoryName))
			.getResultList();
	}

	public List<PostCategoryQueryDto> findAllWithLikesByMemberId(Long memberId, Pageable pageable) {
		String sql = "select new com.techdot.techdot.dto.PostCategoryQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name)" +
			" from Post p" +
			" join p.category c" +
			" join p.likes l" +
			" where l.member.id = :memberId";
			return em.createQuery(sql, PostCategoryQueryDto.class)
				.setParameter("memberId", memberId)
				.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
				.setMaxResults(pageable.getPageSize())
				.getResultList();
	}
}
