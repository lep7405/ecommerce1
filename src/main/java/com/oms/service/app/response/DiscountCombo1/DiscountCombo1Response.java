package com.oms.service.app.response.DiscountCombo1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.enums.DiscountType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountCombo1Response {
	private Long id;
	private DiscountType discountType;


	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;

	public Long productId;
	public String productImage;
	private Integer quantityLimit;
	private Integer purchaseLimit;
	private Integer quantity;
}
