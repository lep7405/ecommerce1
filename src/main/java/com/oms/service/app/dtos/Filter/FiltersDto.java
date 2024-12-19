package com.oms.service.app.dtos.Filter;

import com.oms.service.domain.enums.FilterType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class FiltersDto {
	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 225, message = "Name must be less than 225 characters")
	private String name;
	@NotNull(message = "filterType id is required")
	private FilterType filterType;
	@NotNull(message = "filterIndex id is required")
	private Integer filterIndex;
	@NotNull(message = "categoryId id is required")
	private Long categoryId;
//	@NotNull(message = "attributeId id is required")
	private Long attributeId;
	@NotEmpty(message = "listFiterItemDtos must not be empty")
	private List<FilterItemDto> listFiterItemDtos;
}
