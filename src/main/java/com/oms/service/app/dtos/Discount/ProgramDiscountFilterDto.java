package com.oms.service.app.dtos.Discount;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProgramDiscountFilterDto {
	private String programType;
	private String name;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
}
