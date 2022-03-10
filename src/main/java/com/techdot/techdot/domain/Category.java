package com.techdot.techdot.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter @EqualsAndHashCode(of ="id")
public class Category {

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private CategoryName name; // CS, 알고리즘, 백엔드, 프론트엔드, 보안, DevOps, 자기개발

	@OneToMany(mappedBy = "category")
	private List<Interest> interests = new ArrayList<>();

	@Builder
	public Category(CategoryName name) {
		this.name = name;
	}
}
