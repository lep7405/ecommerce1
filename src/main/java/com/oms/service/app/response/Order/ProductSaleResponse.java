package com.oms.service.app.response.Order;

import com.oms.service.domain.entities.Order.ProductInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ProductSaleResponse {
	private BigDecimal totalPrice;
	private Integer totalQuantity;
	private Long productId;
}
