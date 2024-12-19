package com.oms.service.app.dtos.Payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuerydrPaymentDto {
	private String ipAddress;
	private String txnRef;
	private String orderInfo;
	private String transactionNo;
	private String transactionDate;
	private String requestId;
}
