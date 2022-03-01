package com.techdot.techdot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.techdot.techdot.domain.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileFormDto {

	@NotBlank
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣A-z0-9]{3,20}$", message = "공백없이 문자와 숫자로만 3자 이상 20자 이내로 입력이 가능합니다.")
	@Length(min =3, max=20)
	private String nickname;

	@Length(max = 50)
	private String bio;

	public ProfileFormDto(Member member) {
		this.nickname = member.getNickname();
		this.bio = member.getBio();
	}
}
