package com.oms.service.app.response.product;

import com.oms.service.app.response.AttributeResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantResponse {
	private Long id;
	private BigDecimal price;
	private List<AttributeResponse> listAttributes;
}
