package com.techdot.techdot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.QCategory;
import com.techdot.techdot.domain.QLike;
import com.techdot.techdot.domain.QPost;
import com.techdot.techdot.dto.PostQueryDto;

public class PostRepositoryExtensionImpl extends QuerydslRepositorySupport implements PostRepositoryExtension {

	public PostRepositoryExtensionImpl() {
		super(Post.class);
	}

	@Override
	public List<PostQueryDto> findByKeyword(String keyword, Pageable pageable) {
		QPost post = QPost.post;
		QCategory category = QCategory.category;

		List<PostQueryDto> result = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name, Expressions.FALSE))
			.join(post.category, category)
			.where(post.title.containsIgnoreCase(keyword)
				.or(post.content.containsIgnoreCase(keyword)))
			.offset((pageable.getPageSize() * (pageable.getPageNumber() - 1)))
			.limit(pageable.getPageSize())
			.fetch();

		return result;
	}

	@Override
	public List<PostQueryDto> findWithIsMemberLikeByKeyword(Long memberId, String keyword, Pageable pageable) {
		QPost post = QPost.post;
		QCategory category = QCategory.category;
		QLike like = QLike.like;

		List<PostQueryDto> result = from(post)
			.select(Projections.constructor(PostQueryDto.class,
				post.id, post.title, post.content, post.link, post.writer, post.type,
				post.thumbnailImage, post.uploadDateTime, category.name,
				( from(like).where(like.member.id.eq(memberId).and(like.post.id.eq(post.id)))
					.isNotNull())
			))
			.join(post.category, category)
			.where(post.title.containsIgnoreCase(keyword)
				.or(post.content.containsIgnoreCase(keyword)))
			.offset((pageable.getPageSize() * (pageable.getPageNumber() - 1)))
			.limit(pageable.getPageSize())
			.fetch();

		return result;
	}
}
