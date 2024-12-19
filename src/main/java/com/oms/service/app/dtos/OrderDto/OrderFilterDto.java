package com.oms.service.app.dtos.OrderDto;

import com.oms.service.domain.enums.StateOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class OrderFilterDto {
	private StateOrder stateOrder;
	private Boolean isPayment;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer year;
	private Integer month;
}
