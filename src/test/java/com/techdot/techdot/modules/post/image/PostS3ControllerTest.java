package com.techdot.techdot.modules.post.image;

import static com.techdot.techdot.infra.Constant.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.techdot.techdot.infra.config.LocalStackS3Config;
import com.techdot.techdot.modules.member.auth.WithCurrentUser;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LocalStackS3Config.class)
@AutoConfigureMockMvc
class PostS3ControllerTest {

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private MockMvc mockMvc;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@BeforeEach
	void setUp() {
		amazonS3.createBucket(bucket);
	}

	@WithCurrentUser(value = TEST_EMAIL, role = ADMIN)
	@DisplayName("이미지 업로드 테스트")
	@Test
	void imageUpload() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE,
			"<<png data>>".getBytes());

		mockMvc.perform(multipart("/api/image-upload").file(file).with(csrf())
			.contentType(MediaType.MULTIPART_FORM_DATA)
			.accept(MediaType.APPLICATION_JSON)
			.characterEncoding("UTF-8"))
			.andExpect(status().isOk())
			.andExpect(authenticated());
	}

}