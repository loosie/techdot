package com.techdot.techdot.infra;

import org.testcontainers.utility.DockerImageName;

public final class Constant {
	public static final DockerImageName LOCAL_STACK_IMAGE = DockerImageName.parse("localstack/localstack");
	public static final String MYSQL_IMAGE = "mysql:8";
	public static final String REDIS_IMAGE = "redis:6-alpine";
	public static final String TEST_EMAIL = "test@test.com";
	public static final String USER = "USER";
	public static final String MEMBER = "MEMBER";
	public static final String ADMIN = "ADMIN";
}
