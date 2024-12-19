package com.oms.service.app.response.Refund;

import com.oms.service.app.response.Order.OrderItemResponse;
import com.oms.service.domain.entities.Order.ProductInfo;
import com.oms.service.domain.enums.StateRefundExchange;
import com.oms.service.domain.enums.TypeRefundExchange;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RefundExchangeItemResponse {
	private Long id;
	private TypeRefundExchange typeRefundExchange;
	private StateRefundExchange stateRefundExchange;
	private Integer quantity;
	private BigDecimal fee_exchange;
	private BigDecimal refund_amount;
	private String descriptionAdmin;
	private String description;
	private List<String> images;

	private OrderItemResponse orderItem;

	private ProductInfo productInfo;
}
