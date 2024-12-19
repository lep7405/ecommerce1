package com.oms.service.app.dtos.OrderDto;

import com.oms.service.domain.enums.StateOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto{
	private Boolean isPayment;
	private StateOrder stateOrder;
}
