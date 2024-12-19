package com.oms.service.app.dtos.Refund;

import com.oms.service.domain.enums.StateRefundExchange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRefundExchangeItemDto {
	private StateRefundExchange stateRefundExchange;

	private String descriptionAdmin;
}
