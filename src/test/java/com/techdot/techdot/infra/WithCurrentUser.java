package com.techdot.techdot.infra;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMemberSecurityContextFactory.class)
public @interface WithCurrentUser {

	String value();

	String role();
}
