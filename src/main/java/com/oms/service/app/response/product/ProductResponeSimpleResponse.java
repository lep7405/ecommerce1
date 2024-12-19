package com.oms.service.app.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.app.response.ProductVariantResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
//@Builder
public class ProductResponeSimpleResponse {
	private Long id;
	private String name;
	private String url;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private List<ProductVariantResponse> listProductVariants;
}
