package com.oms.service.app.dtos.Refund;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class RequestRefundExchangeDto {

	@NotEmpty(message = "listRefundExchangeItem must not be null or empty.")
	private List<RefundExchangeItemDto> listRefundExchangeItem;

	private BigDecimal totalFeeRefundExchange;

}
