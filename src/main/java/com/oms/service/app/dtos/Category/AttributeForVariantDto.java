package com.oms.service.app.dtos.Category;

import com.oms.service.domain.enums.DataType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AttributeForVariantDto {
	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 225, message = "Name must be less than 225 characters")
	private String name;

	@NotNull(message = "data type is required")
	private DataType dataType;
	@NotNull(message = "isRequired is required")
	private Boolean isRequired;


}
