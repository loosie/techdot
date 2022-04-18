package com.techdot.techdot.modules.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import com.techdot.techdot.infra.domain.BaseEntity;
import com.techdot.techdot.modules.interest.Interest;
import com.techdot.techdot.modules.like.Like;
import com.techdot.techdot.modules.member.dto.ProfileFormDto;
import com.techdot.techdot.modules.post.Post;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String password;

	private String bio;

	@Column(nullable = false)
	private Boolean emailVerified;

	private String emailCheckToken;

	private LocalDateTime emailCheckTokenSendAt;

	private Integer emailSendTime;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Like> likes = new ArrayList<>();
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Interest> interests = new ArrayList<>();

	@Lob
	private String profileImage;

	@Builder
	public Member(final Long id, final String email, final String nickname, final String password,
		final Boolean emailVerified) {
		Assert.notNull(email, "member.email 값이 존재하지 않습니다.");
		Assert.notNull(nickname, "member.nickname 값이 존재하지 않습니다.");
		Assert.notNull(password, "member.password 값이 존재하지 않습니다.");
		Assert.notNull(emailVerified, "member.emailVerified 값이 존재하지 않습니다.");

		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.emailVerified = emailVerified;
		this.roles.add(Role.ROLE_USER);
		createDateTime();
	}

	public void generateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
		this.emailCheckTokenSendAt = LocalDateTime.now();
		this.emailSendTime = 1;
	}



	public void updateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
	}

	public void completeEmailVerified() {
		updateRoleToMember();
		this.emailVerified = true;
	}

	private void updateRoleToMember() {
		this.roles.add(Role.ROLE_MEMBER);
	}

	public Set<Role> getRoles() {
		return new HashSet<>(this.roles);
	}

	public boolean canSendConfirmEmail() {
		// 5초에 1번씩 총 5회 전송 가능
		if (emailSendTime == 1 ||
			(emailSendTime < 5 && this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusSeconds(5)))) {
			this.emailCheckTokenSendAt = LocalDateTime.now();
			this.emailSendTime += 1;
			return true;
		}

		// 5회 경과시 3분 지나야 재전송 가능
		if (this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusMinutes(3))) {
			this.emailCheckTokenSendAt = LocalDateTime.now();
			this.emailSendTime = 1;
			return true;
		}

		return false;
	}

	public void updateProfile(final ProfileFormDto profileForm) {
		this.nickname = profileForm.getNewNickname();
		this.bio = profileForm.getBio();
		this.profileImage = profileForm.getProfileImage();
		updateDateTime();
	}

	public void updatePassword(final String newPassword) {
		this.password = newPassword;
		updateDateTime();
	}

	public boolean isValidToken(final String token) {
		return this.emailCheckToken.equals(token);
	}

}
