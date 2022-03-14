package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.QCategory;
import com.techdot.techdot.domain.QInterest;
import com.techdot.techdot.domain.QLike;
import com.techdot.techdot.domain.QPost;
import com.techdot.techdot.dto.PostQueryDto;

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
	public List<PostQueryDto> findAllDtoByKeyword(Long memberId, String keyword, Pageable pageable) {
		JPQLQuery<PostQueryDto> query = from(post)
			.select(Projections.constructor(PostQueryDto.class,
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
	public List<PostQueryDto> findAllDto(Long memberId, Pageable pageable) {
		JPQLQuery<PostQueryDto> query = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category);
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryDto> findAllDtoByCategoryName(Long memberId, CategoryName categoryName, Pageable pageable) {
		JPQLQuery<PostQueryDto> query = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.where(category.name.eq(categoryName));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryDto> findAllDtoByLikesMemberId(Long memberId, Pageable pageable) {
		JPQLQuery<PostQueryDto> query = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.join(post.likes, like)
			.where(like.member.id.eq(memberId));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	@Override
	public List<PostQueryDto> findAllDtoByInterestsMemberId(Long memberId, Pageable pageable){
		JPQLQuery<PostQueryDto> query = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, getBooleanExpressionIsMemberLike(memberId)))
			.join(post.category, category)
			.join(category.interests, interest)
			.where(interest.member.id.eq(memberId));
		addSorting(pageable.getSort(), query);
		return getPagingResults(pageable, query);
	}

	private List<PostQueryDto> getPagingResults(Pageable pageable, JPQLQuery<PostQueryDto> query) {
		return query.offset((pageable.getPageSize() * (pageable.getPageNumber() - 1)))
			.limit(pageable.getPageSize())
			.fetch();

	}

	private void addSorting(Sort sort, JPQLQuery<PostQueryDto> query){
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
