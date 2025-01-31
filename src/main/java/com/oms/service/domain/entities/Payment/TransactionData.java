package com.oms.service.domain.entities.Payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionData {
	private String bankCode;
	private String cardType;
	private String orderInfo;
	private String tmnCode;
	private String transactionNo;
	private String txnRef;
	private String SecureHash;
	private String status;
	private String description;
}
