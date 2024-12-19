package com.oms.service.app.response.DisCount;

import com.oms.service.app.response.AttributeValueResponse;
import com.oms.service.domain.enums.DiscountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductVariantDiscountResponse {
	private Long id;
	private List<AttributeValueResponse> listAttributeValue;
	private BigDecimal price;
	private DiscountType discountType;
	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
}
