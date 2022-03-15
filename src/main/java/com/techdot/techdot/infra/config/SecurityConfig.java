package com.techdot.techdot.infra.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final DataSource dataSource;
	private final AccessDeniedHandler accessDeniedHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/join", "/login").not().fullyAuthenticated()
			.mvcMatchers("/", "/check-email",  "/email-login", "/login-by-email", "/confirm-email", "/resend-confirm-email/*", "/error/**",
				"/posts/**", "/category/**",  "/search/**").permitAll()
			.mvcMatchers("/interest/**", "/like/**", "/me/likes", "/accounts/**").access("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
			.mvcMatchers("/new-post", "/post/**", "/accounts/my-upload", "/accounts/settings/category").access("hasRole('ROLE_ADMIN')")
			.anyRequest().authenticated();

		http.formLogin()
			.loginPage("/login").permitAll();

		http.logout()
			.logoutSuccessUrl("/");

		http.rememberMe()
			.userDetailsService(userDetailsService)
			.tokenRepository(tokenRepository())
			.tokenValiditySeconds(60*60*24);

		http.exceptionHandling()
			.accessDeniedPage("/error/403.html")
			.accessDeniedHandler(accessDeniedHandler);
	}

	@Bean
	public PersistentTokenRepository tokenRepository(){
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.mvcMatchers("/node_modules/**")
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
}
