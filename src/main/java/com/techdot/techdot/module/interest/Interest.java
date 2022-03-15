package com.techdot.techdot.module.interest;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import com.techdot.techdot.module.category.Category;
import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.infra.utils.BaseEntity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of ="id", callSuper = false)
public class Interest extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Interest(Member member, Category category) {
		Assert.notNull(member, "interest.member 값이 존재하지 않습니다.");
		Assert.notNull(category, "interest.category 값이 존재하지 않습니다.");

		this.member = member;
		this.category = category;
		createDateTime();
	}
}
