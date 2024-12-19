package com.oms.service.app.response.DiscountHotSale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountHotSaleResponse {
	private Long id;

	private DiscountType discountType;

	private BigDecimal minimumAmount;
	private BigDecimal maximumAmount;

	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
	private String description;

	private Long categoryId;
	private String categoryName;


	public Long productId;
	public String productImage;
	private Long productVariantId;
	private Integer quantityLimit;
	private Integer purchaseLimit;
	private Integer quantity;



}
