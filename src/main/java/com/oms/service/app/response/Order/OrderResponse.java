package com.oms.service.app.response.Order;

import com.oms.service.domain.entities.Order.ProductInfo;
import com.oms.service.domain.entities.UserInfo;
import com.oms.service.domain.enums.StateOrder;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private Long id;
	private UserInfo userInfo;
	private Boolean isPayment;
	private StateOrder stateOrder;
	private Timestamp createdAt;
	private BigDecimal totalPrice;
	List<OrderItemResponse> listOrderItems;

	private String vnpayUrl;
}
