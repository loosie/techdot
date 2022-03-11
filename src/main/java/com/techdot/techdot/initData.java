package com.techdot.techdot;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.domain.Category;
import com.techdot.techdot.domain.CategoryName;
import com.techdot.techdot.domain.Member;
import com.techdot.techdot.domain.Post;
import com.techdot.techdot.domain.PostType;
import com.techdot.techdot.domain.Role;

import lombok.RequiredArgsConstructor;

@Profile("local")
@Component
@RequiredArgsConstructor
public class initData {

	private final InitService initService;

	@PostConstruct
	void init() {
		initService.init();
	}

	@Component
	@Transactional
	@RequiredArgsConstructor
	static class InitService{
		private final EntityManager em;
		private final PasswordEncoder passwordEncoder;

		public void init(){
			Member member = Member.builder()
				.email("jong9712@naver.com")
				.nickname("loosie")
				.password(passwordEncoder.encode("jong9712@naver.com"))
				.emailVerified(true)
				.termsCheck(true)
				.build();
			member.generateEmailCheckToken();
			member.addRole(Role.MEMBER, Role.USER, Role.ADMIN);
			em.persist(member);

			Member member2 = Member.builder()
				.email("test1@naver.com")
				.nickname("loosie2")
				.password(passwordEncoder.encode("test1@naver.com"))
				.emailVerified(true)
				.termsCheck(true)
				.build();
			member2.generateEmailCheckToken();
			em.persist(member2);

			Category cs = Category.builder()
				.name(CategoryName.CS).build();
			em.persist(cs);
			Category backend = Category.builder()
				.name(CategoryName.Backend).build();
			em.persist(backend);
			Category frontend = Category.builder()
				.name(CategoryName.Frontend).build();
			em.persist(frontend);
			Category security = Category.builder()
				.name(CategoryName.Security).build();
			em.persist(security);
			Category DevOps = Category.builder()
				.name(CategoryName.DevOps).build();
			em.persist(DevOps);
			Category Motivation = Category.builder()
				.name(CategoryName.Motivation).build();
			em.persist(Motivation);

			Post shortPost = Post.builder()
				.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + 0)
				.type(PostType.VIDEO)
				.link("http://loosie.tistory.com/" + 122)
				.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
				.writer("loosie")
				.manager(member)
				.category(cs)
				.build();
			em.persist(shortPost);

			for(int i=1; i<=30; i++) {
				Post post = Post.builder()
					.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + i)
					.type(PostType.BLOG)
					.link("http://loosie.tistory.com/" + 123+i)
					.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
					.writer("loosie")
					.manager(member)
					.category(backend)
					.build();
				em.persist(post);
			}

			for(int i=31; i<=60; i++) {
				Post post = Post.builder()
					.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + i)
					.type(PostType.BLOG)
					.link("http://loosie.tistory.com/" + 123+i)
					.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
					.writer("loosie")
					.manager(member)
					.category(frontend)
					.build();
				em.persist(post);
			}

			Post longPost = Post.builder()
				.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + 101)
				.type(PostType.VIDEO)
				.link("http://loosie.tistory.com/" + 1)
				.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌"
					+ "어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
				.writer("loosie")
				.manager(member)
				.category(security)
				.build();
			em.persist(longPost);
		}

	}

}
