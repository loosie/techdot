package com.techdot.techdot.infra;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;

public class MySqlContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	static final MySQLContainer container = new MySQLContainer("mysql:8");
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		container.start();
	}
}
