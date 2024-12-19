package com.oms.service.app.response.Attribute;

import com.oms.service.app.response.AttributeValueResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponseBase {
	private Long id;
	private Long name;
	private List<AttributeValueResponse> listAttributeValue;
}
