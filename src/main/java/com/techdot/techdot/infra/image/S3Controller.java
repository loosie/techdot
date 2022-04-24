package com.techdot.techdot.infra.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	/**
	 * S3에 File 업로드하는 API
	 */
	@PostMapping("/api/image-upload")
	public String imageUpload(MultipartFile file) {
		return s3Service.upload(file);
		// return ResponseEntity.ok(s3Service.upload(file));
	}

}
