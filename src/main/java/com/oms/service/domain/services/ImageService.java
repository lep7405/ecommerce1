package com.oms.service.domain.services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	String createImage(MultipartFile file, String folderName);
}
