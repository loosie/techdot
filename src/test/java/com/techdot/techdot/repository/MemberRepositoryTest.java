package com.techdot.techdot.repository;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.techdot.techdot.domain.Member;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member member;
	@BeforeEach
	void setUp(){
		// given
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.termsCheck(true)
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();

	}

	@DisplayName("멤버 생성하기 - 성공")
	@Test
	void member_create_success() {
		// when
		Member saveMember = memberRepository.save(member);

		// then
		assertEquals(saveMember.getEmail(), member.getEmail());
		assertEquals(saveMember.getNickname(), member.getNickname());
	}

	@DisplayName("이메일로 멤버 존재여부 확인하기")
	@Test
	void member_isExistedByEmail(){
		// when
		memberRepository.save(member);

		// then
		assertTrue(memberRepository.existsByEmail(member.getEmail()));
	}

	@DisplayName("닉네임으로 멤버 존재여부 확인하기")
	@Test
	void member_isExistedByNickname(){
		// when
		 memberRepository.save(member);

		// then
		assertTrue(memberRepository.existsByNickname(member.getNickname()));
	}

	@DisplayName("이메일로 멤버 찾기")
	@Test
	void member_findByEmail(){
		// when
		memberRepository.save(member);

		// then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isPresent());
	}

	@DisplayName("닉네임로 멤버 찾기")
	@Test
	void member_findByNickname(){
		// when
		memberRepository.save(member);

		// then
		assertTrue(memberRepository.findByNickname(member.getNickname()).isPresent());
	}
}