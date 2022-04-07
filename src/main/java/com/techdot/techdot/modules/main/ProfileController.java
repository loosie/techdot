package com.techdot.techdot.modules.main;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
	private final Environment env;

	/**
	 * app profile 체크
	 * @return
	 */
	@GetMapping("/app/profile")
	public String profile() {
		List<String> profiles = Arrays.asList(env.getActiveProfiles());

		List<String> realProfiles = Arrays.asList("real", "real1", "real2");
		String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

		return profiles.stream().filter(realProfiles::contains)
			.findAny().orElse(defaultProfile);
	}
}
