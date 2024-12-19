package com.oms.service.app.dtos.Category;

import com.oms.service.app.dtos.TypeProductDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CategoryDtoForCreate {
	@Valid
	@NotEmpty(message = "parameterDtoList must not be null.")
	private List<ParameterDto> parameterDtoList;
	private List<AttributeDto> listAttributes;

	@NotEmpty(message = "listValueBrandIds must not be null.")
	private List<Long> listValueBrandIds;

	//	@NotNull(message = "typeProductList must not be null.")
	@NotEmpty(message = "listTypeProductDto must not be null.")
	private List<String> typeProductList;
}
