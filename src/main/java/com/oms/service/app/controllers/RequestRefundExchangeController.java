//package com.oms.service.app.controllers;
//
//import com.oms.service.app.dtos.Refund.FilterRefundExchangeDto;
//import com.oms.service.app.dtos.Refund.RequestRefundExchangeDto;
//import com.oms.service.app.dtos.Refund.UpdateRefundExchangeItemDto;
//import com.oms.service.app.dtos.Refund.UpdateRequestRefundExchangeDto;
//import com.oms.service.app.response.ApiResponse;
//import com.oms.service.app.response.Refund.RequestRefundExchangeResponse;
//import com.oms.service.app.response.ResponsePage;
//import com.oms.service.domain.entities.Refund.RequestRefundExchange;
//import com.oms.service.domain.services.RequestRefundExchangeService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/requestRefundExchange")
//@Slf4j
//public class RequestRefundExchangeController {
////	private final RequestRefundExchangeService requestRefundExchangeService;
//	@PostMapping("/orderId/{orderId}")
//	public ApiResponse<RequestRefundExchangeResponse> createRequestRefundExchange(@PathVariable Long orderId,@RequestBody RequestRefundExchangeDto requestRefundExchangeDto) {
//
//		return new ApiResponse<>(HttpStatus.OK.value(), requestRefundExchangeService.createRequestRefundExchange(orderId, requestRefundExchangeDto));
//	}
//
//	@GetMapping("/{id}")
//	public ApiResponse<List<RequestRefundExchangeResponse>> getRequestRefundExchange(@PathVariable Long id) {
//		return new ApiResponse<>(HttpStatus.OK.value(), requestRefundExchangeService.getDetail(id));
//	}
//	@GetMapping("/user")
//	public ApiResponse<ResponsePage<RequestRefundExchange,RequestRefundExchangeResponse>> getAllByUserId(@RequestParam FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable) {
//		return new ApiResponse<>(HttpStatus.OK.value(), requestRefundExchangeService.getAllByUserId(filterRefundExchangeDto, pageable)
//		);
//	}
//
//	@PostMapping("/{id}/calculate")
//	public ApiResponse<RequestRefundExchangeResponse> calculateRequestRefundExchange(@PathVariable Long id, @RequestBody RequestRefundExchangeDto requestRefundExchangeDto) {
//		return new ApiResponse<>(HttpStatus.OK.value(), requestRefundExchangeService.canculateRequestRefundExchange(id, requestRefundExchangeDto)
//		);
//	}
//
//	/**
//	 * Cập nhật RequestRefundExchange.
//	 */
//	@PutMapping("/{id}")
//	public ApiResponse<RequestRefundExchangeResponse> updateRequestRefundExchange(
//			@PathVariable Long id,
//			@RequestBody UpdateRequestRefundExchangeDto updateRequestRefundExchangeDto) {
//		return new ApiResponse<>(
//				HttpStatus.OK.value(),
//				requestRefundExchangeService.updateRequestRefundExchange(id, updateRequestRefundExchangeDto)
//		);
//	}
//
//	/**
//	 * Cập nhật RefundExchangeItem trong một RequestRefundExchange cụ thể.
//	 */
//	@PutMapping("/{requestRefundExchangeId}/items/{refundExchangeItemId}")
//	public ApiResponse<RequestRefundExchangeResponse> updateRefundExchangeItem(
//			@PathVariable Long requestRefundExchangeId,
//			@PathVariable Long refundExchangeItemId,
//			@RequestParam Long orderItemId,
//			@RequestBody UpdateRefundExchangeItemDto updateRefundExchangeItemDto) {
//		return new ApiResponse<>(
//				HttpStatus.OK.value(),
//				requestRefundExchangeService.updateRefundExchangeItem(
//						requestRefundExchangeId, refundExchangeItemId, orderItemId, updateRefundExchangeItemDto)
//		);
//	}
//
//	/**
//	 * Lấy tất cả RequestRefundExchange với bộ lọc và phân trang.
//	 */
//	@GetMapping
//	public ApiResponse<List<RequestRefundExchangeResponse>> getAll(@RequestParam FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable) {
//		return new ApiResponse<>(
//				HttpStatus.OK.value(),
//				requestRefundExchangeService.getAll(filterRefundExchangeDto, pageable)
//		);
//	}
//
//
//	//
//}
