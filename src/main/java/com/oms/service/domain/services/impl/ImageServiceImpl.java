package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.domain.Utils.S3Service;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final S3Service s3Service;
	@Override
	public String createImage(MultipartFile file, String folderName) {
		long startTime = System.currentTimeMillis(); // Thời gian bắt đầu

		if (file.isEmpty()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NO_FILE_SELECTED.val());
		}

		try {
			long checkFileStart = System.currentTimeMillis(); // Thời gian bắt đầu kiểm tra file
			// Kiểm tra file
			if (file.isEmpty()) {
				return ("No file selected");
			}
			long checkFileEnd = System.currentTimeMillis(); // Thời gian kết thúc kiểm tra file
			System.out.println("File validation took: " + (checkFileEnd - checkFileStart) + "ms");

			long uploadStart = System.currentTimeMillis(); // Thời gian bắt đầu upload
			// Tải ảnh lên S3 và lấy tên file
			String filename = s3Service.uploadToS3(file, folderName);
//			String filename= optimizedS3Uploader.uploadToS3Async(file, "brand/").get();
			long uploadEnd = System.currentTimeMillis(); // Thời gian kết thúc upload
			System.out.println("File upload took: " + (uploadEnd - uploadStart) + "ms");

			long urlStart = System.currentTimeMillis(); // Thời gian bắt đầu lấy URL
			// Lấy URL của ảnh đã upload từ S3
			String fileUrl = s3Service.getPresignedUrl(filename);
			long urlEnd = System.currentTimeMillis(); // Thời gian kết thúc lấy URL
			System.out.println("Generating URL took: " + (urlEnd - urlStart) + "ms");

			long endTime = System.currentTimeMillis(); // Thời gian kết thúc tổng
			System.out.println("Total time taken: " + (endTime - startTime) + "ms");

			// Trả về URL của ảnh
			return fileUrl;

		} catch (Exception e) {
			return "Error uploading image: " + e.getMessage();
		}
	}
}
