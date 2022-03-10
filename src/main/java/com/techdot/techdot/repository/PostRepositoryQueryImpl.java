package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.dto.PostQueryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

	private final EntityManager em;

	// TODO: 메인 뷰 조회 (findWithCategoryByCategoryName +  findIdWithLikesAndCategoryByMember) -> 중복 제거하기
	@Override
	public List<PostQueryDto> findQueryDtoByCategoryName(String categoryName, Pageable pageable) {
		String sql =
			"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name)" +
				" from Post p" +
				" join p.category c";
		if (categoryName.equals("All")) {
			return em.createQuery(sql, PostQueryDto.class)
				.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
				.setMaxResults(pageable.getPageSize())
				.getResultList();
		}

		sql += " where p.category.name = :categoryName";
		return em.createQuery(sql, PostQueryDto.class)
			.setParameter("categoryName", CategoryName.valueOf(categoryName))
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

	@Override
	public List<Long> findIdByLikesMemberId(Long memberId, String categoryName) {
		String sql = "select p.id" +
			" from Post p" +
			" join p.category c" +
			" join p.likes l" +
			" where l.member.id = :memberId";
		if (categoryName.equals("All")) {
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

	@Override
	public List<PostQueryDto> findQueryDtoByLikesMemberId(Long memberId, Pageable pageable) {
		String sql =
			"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type, p.thumbnailImage, c.name)" +
				" from Post p" +
				" join p.category c" +
				" join p.likes l" +
				" where l.member.id = :memberId";
		return em.createQuery(sql, PostQueryDto.class)
			.setParameter("memberId", memberId)
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

	@Override
	public List<PostQueryDto> findQueryDtoByInterestsMemberId(Long memberId, Pageable pageable) {
		String sql =
			"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type, p.thumbnailImage, c.name)" +
				" from Post p" +
				" join p.category c" +
				" join c.interests i" +
				" where i.member.id = :memberId";
		return em.createQuery(sql, PostQueryDto.class)
			.setParameter("memberId", memberId)
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}


}
