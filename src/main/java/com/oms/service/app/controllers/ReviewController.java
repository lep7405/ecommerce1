package com.oms.service.app.controllers;

import com.oms.service.app.dtos.Review.ReviewDto;
import com.oms.service.app.response.Review.ReviewResponse;
import com.oms.service.domain.entities.Review;
import com.oms.service.domain.repositories.ReviewRepository;
import com.oms.service.domain.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	@PostMapping("/user/{id}/orderItem/{orderItemId}")
	public Review createReview(@PathVariable("id") Long id,@PathVariable("orderItemId") Long orDerItemId, @RequestBody @Valid ReviewDto reviewDto) {
		return reviewService.createReview(id,orDerItemId,reviewDto);
	}

	@GetMapping("/{id}")
	public ReviewResponse getReview(@PathVariable("id") Long id) {
		return reviewService.getReview(id);
	}

	@PutMapping("/{id}/user/{userId}")
	public ReviewResponse updateReview(@PathVariable("id") Long id,@PathVariable("userId") Long userId,@RequestBody @Valid ReviewDto reviewDto) {
		return reviewService.updateReview(id,userId,reviewDto);
	}
}
