package com.oms.service.app.dtos.Product;

import com.oms.service.domain.enums.DataType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AttributeDto1 {
	@NotNull(message = "attribute id is required")
	private Long id;
	private List<Long> listAttributeValuesIds;
	private String attributeValue;
}
