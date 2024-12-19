package com.oms.service.app.response.DiscountCombo;

import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.app.response.product.ProductVariantResponse;
import com.oms.service.domain.enums.DiscountType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscountComboResponse {
	private Long id;
	private DiscountType discountType;
	private ProductResponeSimpleResponse productMainResponse;
	private List<ProductResponeSimpleResponse> listProductSideResponse;
}
