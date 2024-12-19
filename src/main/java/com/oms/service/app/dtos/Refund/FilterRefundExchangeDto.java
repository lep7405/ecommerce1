package com.oms.service.app.dtos.Refund;

import com.oms.service.domain.enums.StateRefundExchange;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterRefundExchangeDto {
	private StateRefundExchange stateRefundExchange;
	private String sortedBy;
}
