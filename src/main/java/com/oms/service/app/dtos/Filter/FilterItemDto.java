package com.oms.service.app.dtos.Filter;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class FilterItemDto {
	@NotNull(message = "name is required")
	private String name;
	private Integer min;
	private Integer max;
	private Long attributeValueId;
}
