package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeResponse {
	private Long id;
	private String name;
	private DataType dataType;
	private Boolean isRequired;
	private Boolean isSelect;
	private Boolean isSelectMultiple;
	private Boolean isForVariant;
	private List<AttributeValueResponse> listAttributeValue;
	private List<AttributeValueResponse> listAttributeValueCategory;
	private AttributeValueResponse attributeValue;
}
