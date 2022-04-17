package com.techdot.techdot.modules.member;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techdot.techdot.infra.AbstractContainerBaseTest;
import com.techdot.techdot.infra.TCDataJpaTest;

@TCDataJpaTest
class MemberRepositoryTest extends AbstractContainerBaseTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member member;
	@BeforeEach
	void setUp(){
		member = Member.builder()
			.nickname("loosie")
			.password("12345678")
			.email("jong9712@naver.com")
			.emailVerified(false)
			.build();
	}

	@DisplayName("멤버 생성하기 - 성공")
	@Test
	void memberCreate_Success() {
		// given
		Member saveMember = memberRepository.save(member);

		// when, then
		assertEquals(saveMember.getEmail(), member.getEmail());
		assertEquals(saveMember.getNickname(), member.getNickname());
	}

	@DisplayName("이메일로 멤버 존재여부 확인하기")
	@Test
	void memberExistedByEmail_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByEmail(member.getEmail()));
	}

	@DisplayName("닉네임으로 멤버 존재여부 확인하기")
	@Test
	void memberIsExistedByNickname_Success(){
		// given
		 memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.existsByNickname(member.getNickname()));
	}

	@DisplayName("이메일로 멤버 찾기")
	@Test
	void memberFindByEmail_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isPresent());
	}

	@DisplayName("닉네임로 멤버 찾기")
	@Test
	void memberFindByNickname_Success(){
		// given
		memberRepository.save(member);

		// when, then
		assertTrue(memberRepository.findByNickname(member.getNickname()).isPresent());
	}

	@DisplayName("멤버 삭제하기")
	@Test
	void memberDelete_Success(){
		// when
		memberRepository.delete(member);

		// then
		assertTrue(memberRepository.findByEmail(member.getEmail()).isEmpty());
	}

}