package com.techdot.techdot.modules.category;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.techdot.techdot.modules.interest.Interest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter @EqualsAndHashCode(of ="id")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false, unique = true)
	private String title;

	@Column(nullable = false, unique = true)
	private String viewName;


	@OneToMany(mappedBy = "category")
	private List<Interest> interests = new ArrayList<>();

	@Builder
	public Category(String name, String title, String viewName) {
		this.name = name;
		this.title = title;
		this.viewName = viewName;
	}
}
