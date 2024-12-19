package com.oms.service.domain.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Payment.InitPaymentDto;
import com.oms.service.app.dtos.Payment.QuerydrPaymentDto;
import com.oms.service.config.VnPayConfig;
import com.oms.service.domain.Utils.VnPayUtil;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.entities.Payment.PaymentMethod;
import com.oms.service.domain.entities.Payment.TranSactionStatus;
import com.oms.service.domain.entities.Payment.Transaction;
import com.oms.service.domain.enums.StateOrder;
import com.oms.service.domain.enums.StateTransaction;
import com.oms.service.domain.enums.TransactionType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Order.OrderRepository;
import com.oms.service.domain.repositories.Payment.PaymentMethodRepository;
import com.oms.service.domain.repositories.Payment.TransactionStatusRepository;
import com.oms.service.domain.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final VnPayConfig vnPayConfig;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;
	private final OrderRepository orderRepository;
	private final PaymentMethodRepository paymentMethodRepository;
	private final TransactionStatusRepository transactionStatusRepository;
	@Override
	public String createVnPayUrl(InitPaymentDto initPaymentDto) {
		Long amount = initPaymentDto.getAmount() * 100L;

		String ref=initPaymentDto.getTxnRef();
		Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
		vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
		vnpParamsMap.put("vnp_TxnRef", ref);
		vnpParamsMap.put("vnp_IpAddr", initPaymentDto.getIpAddress());

		//build query url
		String queryUrl = VnPayUtil.getPaymentURL(vnpParamsMap, true);
		String hashData = VnPayUtil.getPaymentURL(vnpParamsMap, false);
		String vnpSecureHash = VnPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
		queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
		return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
	}
	@Override
	public String createVnPayUrlQuery(QuerydrPaymentDto querydrPaymentDto) {
		Map<String, String> vnpParamsMap = vnPayConfig.getVNPayQueryConfig();
		vnpParamsMap.put("vnp_RequestId", querydrPaymentDto.getRequestId());
		vnpParamsMap.put("vnp_TxnRef", querydrPaymentDto.getTxnRef());
		vnpParamsMap.put("vnp_OrderInfo", querydrPaymentDto.getOrderInfo());
		vnpParamsMap.put("vnp_TransactionNo",querydrPaymentDto.getTransactionNo());
		vnpParamsMap.put("vnp_IpAddr", querydrPaymentDto.getRequestId());
		vnpParamsMap.put("vnp_TransactionDate", querydrPaymentDto.getTransactionDate());
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnpCreateDate = formatter.format(calendar.getTime());
		vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
		//build query url
		String queryUrl = VnPayUtil.getPaymentURL(vnpParamsMap, true);
		String data=querydrPaymentDto.getRequestId() + "|" + vnpParamsMap.get("vnp_Version") + "|" + vnpParamsMap.get("vnp_Command") + "|" + vnpParamsMap.get("vnp_TmnCode") + "|" + vnpParamsMap.get("vnp_TxnRef") +  "|" + vnpParamsMap.get("vnp_TransactionDate") + "|" +  vnpParamsMap.get("vnp_CreateDate") + "|" +vnpParamsMap.get("vnp_IpAddr") + "|" + vnpParamsMap.get("vnp_OrderInfo");
		log.info(data);
		String vnpSecureHash = VnPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), data);
		queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
		return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
	}

	@Override
	public Transaction createTransaction(HttpServletRequest request) throws JsonProcessingException {
		Map<String, String[]> parameterMap = request.getParameterMap();
		// Lấy trạng thái và mã giao dịch từ request
		String status = request.getParameter("vnp_TransactionStatus");
		String ref = request.getParameter("vnp_TxnRef");
		String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request.getParameterMap());

		// Tìm Order và PaymentMethod tương ứng
		Order order = orderRepository.findById(Long.valueOf(ref)).orElseThrow(() ->
				new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_NOT_FOUND.val()));

		PaymentMethod paymentMethod = paymentMethodRepository.findById(order.getPaymentMethodId()).orElseThrow(() ->
				new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PAYMENT_METHOD_NOT_FOUND.val()));

		// Tìm trạng thái giao dịch tương ứng
		TranSactionStatus transactionStatus = transactionStatusRepository.findByStatus(status,paymentMethod.getId()).orElseThrow(() ->
				new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.TRANSACTION_STATUS_NOT_FOUND.val()));

		// Xử lý trạng thái giao dịch
		processTransactionStatus(order, transactionStatus,paymentMethod,jsonString);

		return null;
	}

	private void processTransactionStatus(Order order, TranSactionStatus transactionStatus, PaymentMethod paymentMethod,String transactionData) {
		// Cập nhật thông tin đơn hàng và transaction
		order.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		Transaction transaction = createTransaction(order,paymentMethod,transactionData);

		// Xử lý trạng thái thanh toán
		switch (transactionStatus.getTransactionStatusState()) {
			case SUCCESS:
				updateOrderAndTransaction(order, transaction, StateOrder.SUCCESS, TransactionType.PAYMENT, true, StateTransaction.SUCCESS);
				break;
			case FAILED:
				updateOrderAndTransaction(order, transaction, StateOrder.PAYMENT_FAILED, TransactionType.PAYMENT, false, StateTransaction.FAILED);
				break;
			case FAILED_AND_REFUND:
				updateOrderAndTransaction(order, transaction, StateOrder.PAYMENT_FAILED_AND_REFUND, TransactionType.PAYMENT_REFUND, false, StateTransaction.FAILED);
				break;
			case FAILED_AND_REFUND_SUCCESS:
				updateOrderAndTransaction(order, transaction, StateOrder.PAYMENT_FAILED_AND_REFUND_SUCCESS, TransactionType.PAYMENT_REFUND, false, StateTransaction.SUCCESS);
				break;
			case FAILED_AND_REFUND_FAILED:
				updateOrderAndTransaction(order, transaction, StateOrder.PAYMENT_FAILED_AND_REFUND_FAILED, TransactionType.PAYMENT_REFUND, false, StateTransaction.FAILED);
				break;
			default:
				throw new IllegalArgumentException("Unknown transaction status: " + transactionStatus);
		}
	}

	private void updateOrderAndTransaction(Order order, Transaction transaction, StateOrder stateOrder, TransactionType transactionType, boolean isPayment, StateTransaction stateTransaction) {
		// Cập nhật trạng thái đơn hàng
		order.setStateOrder(stateOrder);
		order.setIsPayment(isPayment);
		// Cập nhật transaction
		transaction.setTransactionType(transactionType);
		transaction.setStateTransaction(stateTransaction);
		order.addTransaction(transaction);
		orderRepository.save(order);
	}

	private Transaction createTransaction(Order order,PaymentMethod paymentMethod,String jsonString) {
		// Tạo một đối tượng Transaction mới
		Transaction transaction = new Transaction();
		transaction.setAmount(order.getTotalPrice());
		transaction.setPayDate(Timestamp.valueOf(LocalDateTime.now()));
		transaction.setTransactionData(jsonString);
		transaction.setPaymentMethod(paymentMethod);
		transaction.setPaymentMethodTransaction(paymentMethod.getName());
		return transaction;
	}

}
