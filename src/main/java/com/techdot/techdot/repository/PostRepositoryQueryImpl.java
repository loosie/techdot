package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.dto.PostQueryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

	private final EntityManager em;
	private static final String POST_QUERY_DTO_SQL =
		"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type, p.thumbnailImage, p.uploadDateTime, c.name, false)"
			+
			" from Post p" +
			" join p.category c";
	private static final String POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE =
		"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, p.uploadDateTime, c.name,"
			+ "((select count(l.id) from Like l where l.member.id = :memberId and l.post.id = p.id)>0))" +
			" from Post p" +
			" join p.category c";
	private static final String JOIN_LIKES_WHERE_MEMBER_ID = " join p.likes l where l.member.id = :memberId";

	@Override
	public List<PostQueryDto> findByCategoryName(String categoryName, Pageable pageable) {
		return getPagingResult(
			getQueryDtoByCategoryName(POST_QUERY_DTO_SQL, categoryName, pageable.getSort()),
			pageable);
	}

	@Override
	public List<PostQueryDto> findWithIsMemberLikeByCategoryName(Long memberId, String categoryName,
		Pageable pageable) {
		return getPagingResult(
			getQueryDtoByCategoryName(POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE, categoryName,
				pageable.getSort()).setParameter("memberId", memberId),
			pageable);
	}

	private String getSort(String query, Sort sort) {
		if (sort.toString().contains("uploadDateTime")) {
			query += " order by p.uploadDateTime desc";
		} else {
			query += " order by p.id desc";
		}
		return query;
	}

	private TypedQuery<PostQueryDto> getQueryDtoByCategoryName(String query, String categoryName,
		Sort sort) {
		TypedQuery<PostQueryDto> result;
		if (categoryName.equals("All")) {
			result = getPostQueryDto(query, sort);
		} else {
			result = getPostQueryDto(query +
				" where p.category.name = :categoryName", sort)
				.setParameter("categoryName", CategoryName.valueOf(categoryName));
		}
		return result;
	}

	@Override
	public List<PostQueryDto> findByLikesMemberId(Long memberId, Pageable pageable) {
		return getPagingResult(
			getPostQueryDto(POST_QUERY_DTO_SQL + JOIN_LIKES_WHERE_MEMBER_ID, pageable.getSort())
				.setParameter("memberId", memberId),
			pageable);
	}

	@Override
	public List<PostQueryDto> findWithIsMemberLikeByInterestsMemberId(Long memberId, Pageable pageable) {
		return getPagingResult(
			getPostQueryDto(
				POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE + " join c.interests i where i.member.id = :memberId",
				pageable.getSort())
				.setParameter("memberId", memberId), pageable);
	}

	private TypedQuery<PostQueryDto> getPostQueryDto(String query, Sort sort) {
		return em.createQuery(getSort(query, sort), PostQueryDto.class);
	}

	private List<PostQueryDto> getPagingResult(TypedQuery<PostQueryDto> typedQuery, Pageable pageable) {
		return typedQuery
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

}
