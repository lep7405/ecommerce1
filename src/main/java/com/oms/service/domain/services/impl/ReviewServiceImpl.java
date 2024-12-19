package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Review.ImagDto;
import com.oms.service.app.dtos.Review.ReviewDto;
import com.oms.service.app.response.Review.ReviewResponse;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Order.OrderItem;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Order.OrderItemRepository;
import com.oms.service.domain.repositories.ProductRepository;
import com.oms.service.domain.repositories.ReviewRepository;
import com.oms.service.domain.repositories.UserRepository;
import com.oms.service.domain.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final ModelMapper mapper;


	@Override
	@Transactional
	public Review createReview(Long userId,Long OrderItemId,ReviewDto reviewDto) {
		User user=userRepository.findById(userId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val));
		OrderItem orderItem=orderItemRepository.findById(OrderItemId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_ITEM_NOT_FOUND.val));
		Product product= productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND.val));
		Optional<OrderItem> orderItemChecked=user.getListOrders().stream().flatMap(order -> order.getListOrderItems().stream())
				.filter(orderItem1 -> orderItem1.getId().equals(orderItem.getId()))
				.findFirst();
		if(orderItemChecked.isEmpty()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.NOT_FOUND_ORDER_ITEM.val());
		}
		if(orderItem.getReview()!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REVIEW_EXISTS.val);
		}
		Review review=new Review();
		review.setDeleted(false);
		review.setRateNumber(reviewDto.getRateNumber());
		review.setReviewBody(reviewDto.getReviewBody());
		review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		if(reviewDto.getListImagDto()!=null&&!reviewDto.getListImagDto().isEmpty()){
			for (ImagDto imagDto : reviewDto.getListImagDto()) {
				Imag imag=new Imag();
				imag.setUrl(imagDto.getUrl());
				imag.setDeleted(false);
				imag.setIsCover(imagDto.getIsCover());
//				review.addImag(imag);
			}
		}
		review.setOrderitem(orderItem);
		review.setUser(user);
		String jsonField=null;

		if(product.getAverageReview()==null){
			product.setAverageReview(new AverageReview());
		}
		modifyReviewFields(reviewDto.getRateNumber(),orderItem.getProductId(),true);

		reviewRepository.save(review);
		return review;
	}

	@Override
	public ReviewResponse getReview(Long id) {
		Review review=reviewRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REVIEW_NOT_FOUND.val));
		return mapper.map(review, ReviewResponse.class);
	}

	public List<ReviewResponse> getReviewByUser(Long userId){
		User user=userRepository.findById(userId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val));
		List<Review> listReview=user.getListReview();
		List<ReviewResponse> listReviewResponse=new ArrayList<>();
		for(Review review:listReview){
			listReviewResponse.add(mapper.map(review, ReviewResponse.class));
		}
		return listReviewResponse;
	}

	@Override
	@Transactional
	public ReviewResponse updateReview(Long id,Long userId,ReviewDto reviewDto){
		User user=userRepository.findById(userId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val));
		if(user.getListReview().stream().map(Review::getId).anyMatch(reviewId -> reviewId.equals(id))){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REVIEW_NOT_FOUND.val);
		}
		Review review=reviewRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REVIEW_NOT_FOUND.val));
		review.setReviewBody(reviewDto.getReviewBody());
		if(!Objects.equals(review.getRateNumber(), reviewDto.getRateNumber())){
			review.setRateNumber(reviewDto.getRateNumber());
			modifyReviewFields(reviewDto.getRateNumber(),review.getOrderitem().getProductId(),true);
			modifyReviewFields(review.getRateNumber(),review.getOrderitem().getProductId(),false);
		}
		review.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		if(reviewDto.getListImagDto()!=null&&!reviewDto.getListImagDto().isEmpty()){
			List<ImagDto> imagDtoListNew=reviewDto.getListImagDto().stream().filter(imagDto -> imagDto.getId()==null).toList();

			List<Long> listImagDto=reviewDto.getListImagDto().stream().map(ImagDto::getId).toList();
//			List<Long> listImag=review.getListImag().stream().map(Imag::getId).toList();

//			List<ImagDto> listImagDtoUpdate=reviewDto.getListImagDto().stream().filter(imagDto -> listImag.contains(imagDto.getId())).toList();
//			List<Long> listImagIdDelete=reviewDto.getListImagDto().stream().map(ImagDto::getId).filter(imagDtoId -> !listImag.contains(imagDtoId)).toList();

			for (ImagDto imagDto : imagDtoListNew) {
				Imag imag=new Imag();
				imag.setUrl(imagDto.getUrl());
				imag.setDeleted(false);
				imag.setIsCover(imagDto.getIsCover());
//				review.addImag(imag);
			}
//			for(ImagDto imagDto:listImagDtoUpdate){
//				Imag imag=review.getListImag().stream().filter(imag1 -> imag1.getId().equals(imagDto.getId())).findFirst().orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.IMAG_NOT_FOUND.val));
//				imag.setUrl(imagDto.getUrl());
//				imag.setIsCover(imagDto.getIsCover());
//			}
//			for(Long ids:listImagIdDelete){
//				Imag imag=review.getListImag().stream().filter(imag1 -> imag1.getId().equals(ids)).findFirst().orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.IMAG_NOT_FOUND.val));
//				imag.setDeleted(true);
//			}
		}
		reviewRepository.save(review);
		return mapper.map(review, ReviewResponse.class);
	}

	private void modifyReviewFields(Integer rateNumber, Long productId, boolean isIncrease) {
		String jsonField = switch (rateNumber) {
			case 1 -> "numberOf1stars";
			case 2 -> "numberOf2stars";
			case 3 -> "numberOf3stars";
			case 4 -> "numberOf4stars";
			case 5 -> "numberOf5stars";
			default -> throw new IllegalArgumentException("Invalid rate number: " + rateNumber);
		};

		if (isIncrease) {
			productRepository.modifyReviewField(productId, jsonField,"increment");
		} else {
			productRepository.modifyReviewField(productId, jsonField,"decrement");
		}
	}
}
