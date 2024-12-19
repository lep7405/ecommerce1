package com.oms.service.app.dtos.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor

public class AttributeValueDto {
//	@NotNull(message = "attribute value id is required")
	private Long id;
//	@NotBlank(message = "attValue is required")
//	@Size(min = 1, max = 225, message = "attValue must be less than 225 characters")
	private Object attValue;
}
