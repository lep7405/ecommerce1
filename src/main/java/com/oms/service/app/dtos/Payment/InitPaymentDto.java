package com.oms.service.app.dtos.Payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class InitPaymentDto {
	private String ipAddress;
	private Long amount;
	private String txnRef;
}
