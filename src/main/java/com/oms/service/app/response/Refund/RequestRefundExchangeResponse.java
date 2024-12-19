package com.oms.service.app.response.Refund;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestRefundExchangeResponse {
	private Long id;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private BigDecimal totalFeeRefundExchange;
	private List<RefundExchangeItemResponse> listRefundExchangeItems;
}
