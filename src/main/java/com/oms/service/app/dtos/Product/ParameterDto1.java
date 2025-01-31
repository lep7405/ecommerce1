package com.oms.service.app.dtos.Product;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ParameterDto1 {
	@NotNull(message = "parameter id is required")
	private Long id;
	@NotEmpty(message = "list attribute dto is required")
	 private List<AttributeDto1> listAttributes;
}
