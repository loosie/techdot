package com.techdot.techdot.modules.post;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.techdot.techdot.modules.interest.QInterest;
import com.techdot.techdot.modules.like.QLike;
import com.techdot.techdot.modules.post.dto.PostQueryResponseDto;

public class PostRepositoryExtensionImpl extends QuerydslRepositorySupport implements PostRepositoryExtension {

	private final QPost post;
	private final QCategory category;
	private final QLike like;
	private final QInterest interest;

	public PostRepositoryExtensionImpl() {
		super(Post.class);
		post = QPost.post;
		category = QCategory.category;
		like = QLike.like;
		interest = QInterest.interest;
	}

	@Override
	public List<PostQueryResponseDto> findAllDtoByKeyword(Long memberId, String keyword, Pageable pageable) {
		JPQLQuery<PostQueryResponseDto> query = from(post)
			.select(Projections.constructor(PostQueryResponseDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.where(post.title.containsIgnoreCase(keyword)
				.or(post.content.containsIgnoreCase(keyword))
				.or(post.writer.containsIgnoreCase(keyword)));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryResponseDto> findAllDto(Long memberId, Pageable pageable) {
		JPQLQuery<PostQueryResponseDto> query = from(post)
			.select(Projections.constructor(PostQueryResponseDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category);
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryResponseDto> findAllDtoByCategoryName(Long memberId, CategoryName categoryName, Pageable pageable) {
		JPQLQuery<PostQueryResponseDto> query = from(post)
			.select(Projections.constructor(PostQueryResponseDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.where(category.name.eq(categoryName));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryResponseDto> findAllDtoByLikesMemberId(Long memberId, Pageable pageable) {
		JPQLQuery<PostQueryResponseDto> query = from(post)
			.select(Projections.constructor(PostQueryResponseDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.join(post.likes, like)
			.where(like.member.id.eq(memberId));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryResponseDto> findAllDtoByInterestsMemberId(Long memberId, Pageable pageable){
		JPQLQuery<PostQueryResponseDto> query = from(post)
			.select(Projections.constructor(PostQueryResponseDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.join(category.interests, interest)
			.where(interest.member.id.eq(memberId));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	private List<PostQueryResponseDto> getPagingResults(Pageable pageable, JPQLQuery<PostQueryResponseDto> query) {
		return query.offset((pageable.getPageSize() * (pageable.getPageNumber() - 1)))
			.limit(pageable.getPageSize())
			.fetch();

	}

	private void addSorting(Sort sort, JPQLQuery<PostQueryResponseDto> query){
		if (sort.toString().contains("uploadDateTime")) {
			query.orderBy(post.uploadDateTime.desc());
		} else {
			query.orderBy(post.createdDateTime.desc());
		}
	}

	private BooleanExpression getBooleanExpressionIsMemberLike(Long memberId) {
		BooleanExpression isMemberLike = Expressions.FALSE;
		if (memberId != -1L) {
			isMemberLike = from(like).where(like.member.id.eq(memberId).and(like.post.id.eq(post.id)))
				.isNotNull();
		}
		return isMemberLike;
	}
}
