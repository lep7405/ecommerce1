package com.oms.service.app.dtos.Refund;

import com.oms.service.domain.enums.StateRefundExchange;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateRequestRefundExchangeDto {
	@NotNull(message="stateRefundExchange must not be null.")
	private StateRefundExchange stateRefundExchange;
}
