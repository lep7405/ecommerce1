package com.oms.service.domain.services;

import com.oms.service.app.dtos.Review.ReviewDto;
import com.oms.service.app.response.Review.ReviewResponse;
import com.oms.service.domain.entities.Review;

public interface ReviewService {
	Review createReview(Long userId,Long OrderItemId,ReviewDto reviewDto);
	ReviewResponse getReview(Long id);

	ReviewResponse updateReview(Long id,Long userId,ReviewDto reviewDto);
}
