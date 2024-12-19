package com.oms.service.app.dtos.Category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class ParameterDto {
	private Long id;
	@Valid
	@NotEmpty(message = "listAttributeDto is required")
	private List<AttributeDto> listAttributeDto;

	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 225, message = "Name must be less than 225 characters")
	private String name;

	@NotNull(message = "groupIndex id is required")
	private Integer groupIndex;
}
