package com.techdot.techdot.modules.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		Assert.notNull(email, "member.email ?????? ???????????? ????????????.");
		Assert.notNull(nickname, "member.nickname ?????? ???????????? ????????????.");
		Assert.notNull(password, "member.password ?????? ???????????? ????????????.");
		Assert.notNull(emailVerified, "member.emailVerified ?????? ???????????? ????????????.");

		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.emailVerified = emailVerified;
		this.roles.add(Role.ROLE_USER);
		createDateTime();
	}

	public void countEmailSendTime() {
		this.emailCheckTokenSendAt = LocalDateTime.now();
		this.emailSendTime = 1;
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
		// 5?????? 1?????? ??? 5??? ?????? ??????
		if (emailSendTime == 1 ||
			(emailSendTime < 5 && this.emailCheckTokenSendAt.isBefore(LocalDateTime.now().minusSeconds(5)))) {
			this.emailCheckTokenSendAt = LocalDateTime.now();
			this.emailSendTime += 1;
			return true;
		}

		// 5??? ????????? 3??? ????????? ????????? ??????
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

}
