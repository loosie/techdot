package com.techdot.techdot.modules.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(final String email);

	boolean existsByNickname(final String nickname);

	Optional<Member> findByEmail(final String email);

	Optional<Member> findByNickname(final String nickname);
}
