package com.oms.service.app.dtos.Review;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ImagDto {
	private Long id;
	@NotNull(message = "Url is required")
	private String url;
	private Boolean isCover;
}
