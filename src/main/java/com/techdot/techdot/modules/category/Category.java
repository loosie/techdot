package com.techdot.techdot.modules.category;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import com.techdot.techdot.modules.category.dto.CategoryFormDto;
import com.techdot.techdot.modules.interest.Interest;
import com.techdot.techdot.modules.post.Post;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String viewName;

	@Column(nullable = false)
	private String name; // nav display name

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private Integer displayOrder;

	@OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
	private List<Interest> interests = new ArrayList<>();

	@OneToMany(mappedBy = "category")
	private List<Post> posts = new ArrayList<>();

	@Builder
	public Category(final String viewName, final String name, final String title, final Integer displayOrder) {
		Assert.notNull(viewName, "category.viewName 값이 존재하지 않습니다.");
		Assert.notNull(name, "category.name 값이 존재하지 않습니다.");
		Assert.notNull(title, "category.title 값이 존재하지 않습니다.");
		Assert.notNull(displayOrder, "category.displayOrder 값이 존재하지 않습니다.");

		this.viewName = viewName;
		this.name = name;
		this.title = title;
		this.displayOrder = displayOrder;
	}

	public void update(final CategoryFormDto categoryForm) {
		this.viewName = categoryForm.getViewName();
		this.name = categoryForm.getName();
		this.title = categoryForm.getTitle();
		this.displayOrder = categoryForm.getDisplayOrder();
	}

	public void updateCategoryOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
}
