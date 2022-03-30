package com.techdot.techdot.modules.main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class ProfileControllerTest {

	private ProfileController profileController;

	@DisplayName("spring profile 조회하기")
	@Test
	void real_profile_조회(){
		// given
		String expectedProfile = "real";
		MockEnvironment env = new MockEnvironment();
		env.addActiveProfile(expectedProfile);
		env.addActiveProfile("oauth");
		env.addActiveProfile("real-db");

		// when
		profileController = new ProfileController(env);
		String profile = profileController.profile();

		// then
		assertEquals(profile, expectedProfile);
	}
}