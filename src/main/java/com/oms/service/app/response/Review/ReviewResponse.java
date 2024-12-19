package com.oms.service.app.response.Review;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewResponse {
	private Long id;
	private String reviewBody;
	private Integer rateNumber;
	private List<ImagResponse> listImag;
}
