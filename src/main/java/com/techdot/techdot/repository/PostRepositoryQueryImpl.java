package com.techdot.techdot.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.dto.PostQueryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostRepositoryQueryImpl implements PostRepositoryQuery {

	private final EntityManager em;
	private static final String POST_QUERY_DTO_SQL =
		"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name, false)" +
			" from Post p" +
			" join p.category c";
	private static final String POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE =
		"select new com.techdot.techdot.dto.PostQueryDto(p.id, p.title, p.content, p.link, p.writer, p.type,  p.thumbnailImage, c.name,"
			+ "((select count(l.id) from Like l where l.member.id = :memberId and l.post.id = p.id)>0))" +
			" from Post p" +
			" join p.category c";
	private static final String JOIN_LIKES_WHERE_MEMBER_ID = " join p.likes l where l.member.id = :memberId";


	@Override
	public List<PostQueryDto> findQueryDtoByCategoryName_ifMember_withIsMemberLike(Long memberId, String categoryName, Pageable pageable) {
		String sql = POST_QUERY_DTO_SQL;
		if(memberId != null) {
			sql = POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE;
		}

		TypedQuery<PostQueryDto> result;
		if (categoryName.equals("All")) {
			result = getPostQueryDto(sql);
		} else {
			result = getPostQueryDto(sql +
				" where p.category.name = :categoryName")
				.setParameter("categoryName", CategoryName.valueOf(categoryName));
		}

		if(memberId != null) result = result.setParameter("memberId", memberId);

		return getPagingResult(result, pageable);
	}


	@Override
	public List<PostQueryDto> findQueryDtoByLikesMemberId(Long memberId, Pageable pageable) {
		return getPagingResult(
			getPostQueryDto(POST_QUERY_DTO_SQL + JOIN_LIKES_WHERE_MEMBER_ID)
				.setParameter("memberId", memberId),
			pageable);
	}

	@Override
	public List<PostQueryDto> findQueryDtoWithIsMemberLikeByInterestsMemberId(Long memberId, Pageable pageable) {
		return getPagingResult(
			getPostQueryDto(
				POST_QUERY_DTO_SQL_WITH_IS_MEMBER_LIKE + " join c.interests i where i.member.id = :memberId")
				.setParameter("memberId", memberId), pageable);
	}

	private TypedQuery<PostQueryDto> getPostQueryDto(String query) {
		return em.createQuery(query, PostQueryDto.class);
	}

	private List<PostQueryDto> getPagingResult(TypedQuery<PostQueryDto> typedQuery, Pageable pageable) {
		return typedQuery
			.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber() - 1))
			.setMaxResults(pageable.getPageSize())
			.getResultList();
	}

}
