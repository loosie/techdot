package com.techdot.techdot.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.techdot.techdot.config.auth.PrincipalDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final PrincipalDetailsService principalsDetailsService;
	private final DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/", "/join", "/login", "/check-email",  "/email-login", "/login-by-email", "/confirm-email", "/resend-confirm-email/*",
				"/post/scrollList", "/display"
			).permitAll()
			.anyRequest().authenticated();

		http.formLogin()
			.loginPage("/login").permitAll();

		http.logout()
			.logoutSuccessUrl("/");

		http.rememberMe()
			.userDetailsService(principalsDetailsService)
			.tokenRepository(tokenRepository())
			.tokenValiditySeconds(60*60*24);
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
