package com.oms.service.domain.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oms.service.app.dtos.Payment.InitPaymentDto;
import com.oms.service.app.dtos.Payment.QuerydrPaymentDto;
import com.oms.service.domain.entities.Payment.Transaction;

import javax.servlet.http.HttpServletRequest;

public interface PaymentService {
	String createVnPayUrl(InitPaymentDto initPaymentDto);
	String createVnPayUrlQuery(QuerydrPaymentDto querydrPaymentDto);

	Transaction createTransaction(HttpServletRequest request) throws JsonProcessingException;
}
