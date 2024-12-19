package com.oms.service.app.dtos.Review;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
	@NotNull(message = "Review body is required")
	private String reviewBody;

	@NotNull(message = "Rate number is required")
	@Min(value = 1, message = "Rate number must be at least 1")
	@Max(value = 5, message = "Rate number must not be greater than 5")
	private Integer rateNumber;

	private List<ImagDto> listImagDto;
}
