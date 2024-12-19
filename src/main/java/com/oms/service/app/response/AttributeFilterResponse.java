package com.oms.service.app.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class AttributeFilterResponse {
//	private BigDecimal minPrice;
//	private BigDecimal maxPrice;
	private List<AttributeResponse> listAttributeResponse;
}
