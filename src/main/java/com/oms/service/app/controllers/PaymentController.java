package com.oms.service.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Payment.QuerydrPaymentDto;
import com.oms.service.domain.Utils.VnPayUtil;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.entities.Payment.PaymentMethod;
import com.oms.service.domain.entities.Payment.TranSactionStatus;
import com.oms.service.domain.entities.Payment.Transaction;
import com.oms.service.domain.enums.StateOrder;
import com.oms.service.domain.enums.StateTransaction;
import com.oms.service.domain.enums.TransactionStatusState;
import com.oms.service.domain.enums.TransactionType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Order.OrderRepository;
import com.oms.service.domain.repositories.Payment.PaymentMethodRepository;
import com.oms.service.domain.repositories.Payment.TransactionRepository;
import com.oms.service.domain.repositories.Payment.TransactionStatusRepository;
import com.oms.service.domain.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.DataException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Slf4j
public class PaymentController {
	private final OrderRepository orderRepository;
	private final TransactionStatusRepository transactionStatusRepository;
	private final PaymentMethodRepository paymentMethodRepository;
	private final TransactionRepository transactionRepository;
	private final ObjectMapper objectMapper;
	private final PaymentService paymentService;

	@Transactional
	@GetMapping("/vn-pay-callback")
	public String payCallbackHandler(HttpServletRequest request) throws Exception {
		paymentService.createTransaction(request);
		return "success";
	}

	@Transactional
	@GetMapping("/refund")
	public String refund(HttpServletRequest request) throws Exception {
		Transaction transaction=transactionRepository.findById(7L).orElse(null);
		String transactionData=transaction.getTransactionData();
		log.info("trans"+transactionData);
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, List<String>> parameterMap = objectMapper.readValue(transactionData, Map.class);

		// Duyệt qua các cặp key-value và in ra
		for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			System.out.print("Key: " + key + " => ");
			System.out.println("Values: " + String.join(", ", values));
		}
		String he=parameterMap.get("vnp_PayDate").get(0);
		String vnpPayDate = "20241130010521";

		// Định dạng để parse chuỗi thành Date
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = inputFormat.parse(vnpPayDate);

		// Tạo Calendar và thiết lập timezone (GMT+7)
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		calendar.setTime(date);

		// Định dạng lại Date thành chuỗi theo yêu cầu (yyyyMMddHHmmss)
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String formattedDate = outputFormat.format(calendar.getTime());
		QuerydrPaymentDto querydrPaymentDto= QuerydrPaymentDto.builder()
				.txnRef("17")
				.orderInfo(parameterMap.get("vnp_OrderInfo").get(0))
				.transactionNo(parameterMap.get("vnp_TransactionNo").get(0))
				.requestId(VnPayUtil.getIpAddress(request))
				.transactionDate(vnpPayDate)
				.build();
		return paymentService.createVnPayUrlQuery(querydrPaymentDto);
	}
}
