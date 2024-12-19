package com.oms.service.app.response.Order;

import com.oms.service.domain.entities.Order.ProductInfo;
import com.oms.service.domain.entities.Order.ProductVariantInfo;
import com.oms.service.domain.enums.StateOrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
	private Long id;
	private Long productId;
	private Long productVariantId;
	private StateOrderItem stateOrderItem;
	private ProductInfo productInfo;

}
