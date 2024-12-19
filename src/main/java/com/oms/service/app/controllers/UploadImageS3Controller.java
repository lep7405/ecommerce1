package com.oms.service.app.controllers;

import com.oms.service.domain.Utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class UploadImageS3Controller {
	private final S3Service s3Service;
	@PostMapping
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,@RequestParam("folderName") String folderName) throws IOException {
		return ResponseEntity.ok(s3Service.uploadToS3(file, folderName));
	}
}
