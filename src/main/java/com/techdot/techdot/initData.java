package com.techdot.techdot;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techdot.techdot.module.category.Category;
import com.techdot.techdot.module.category.CategoryName;
import com.techdot.techdot.module.member.Member;
import com.techdot.techdot.module.post.Post;
import com.techdot.techdot.module.post.PostType;
import com.techdot.techdot.module.member.Role;

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
				.build();
			member.generateEmailCheckToken();
			member.addRole(Role.ROLE_MEMBER, Role.ROLE_ADMIN);
			em.persist(member);

			Member member2 = Member.builder()
				.email("test1@naver.com")
				.nickname("loosie2")
				.password(passwordEncoder.encode("test1@naver.com"))
				.emailVerified(true)
				.build();
			member2.addRole(Role.ROLE_MEMBER);
			member2.generateEmailCheckToken();
			em.persist(member2);

			Member member3 = Member.builder()
				.email("test2@naver.com")
				.nickname("loosie3")
				.password(passwordEncoder.encode("test2@naver.com"))
				.emailVerified(false)
				.build();
			member3.generateEmailCheckToken();
			em.persist(member3);

			Category cs = Category.builder()
				.name(CategoryName.CS).build();
			em.persist(cs);
			Category backend = Category.builder()
				.name(CategoryName.BACKEND).build();
			em.persist(backend);
			Category frontend = Category.builder()
				.name(CategoryName.FRONTEND).build();
			em.persist(frontend);
			Category security = Category.builder()
				.name(CategoryName.SECURITY).build();
			em.persist(security);
			Category DevOps = Category.builder()
				.name(CategoryName.DEV_OPS).build();
			em.persist(DevOps);
			Category Motivation = Category.builder()
				.name(CategoryName.MOTIVATION).build();
			em.persist(Motivation);

			Post shortPost = Post.builder()
				.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + 0)
				.type(PostType.VIDEO)
				.link("http://loosie.tistory.com/" + 122)
				.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
				.writer("loosie")
				.manager(member)
				.category(cs)
				.uploadDateTime(LocalDateTime.now())
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
					.uploadDateTime(LocalDateTime.now().minusDays(30+i))
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
					.uploadDateTime(LocalDateTime.now().minusDays(i-30))
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
				.uploadDateTime(LocalDateTime.now())
				.build();
			em.persist(longPost);

			Post longPost1 = Post.builder()
				.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + 102)
				.type(PostType.VIDEO)
				.link("http://loosie.tistory.com/" + 11)
				.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌"
					+ "어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
				.writer("loosie")
				.manager(member)
				.category(DevOps)
				.uploadDateTime(LocalDateTime.now().minusDays(9))
				.build();
			em.persist(longPost1);

			Post longPost2 = Post.builder()
				.title("loosie 티스토리 블로그 미리보기 techDot 기술 큐레이션 서비스" + 103)
				.type(PostType.VIDEO)
				.link("http://loosie.tistory.com/" + 111)
				.content("어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌"
					+ "어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저"
					+ "쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌어쩌저쩌")
				.writer("loosie")
				.manager(member)
				.category(Motivation)
				.uploadDateTime(LocalDateTime.now().minusDays(10))
				.build();
			em.persist(longPost2);
		}

	}

}
