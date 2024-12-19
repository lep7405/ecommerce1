//package com.oms.service.domain.services.impl;
//
//import com.ommanisoft.common.exceptions.ExceptionOm;
//import com.oms.service.app.dtos.Refund.*;
//import com.oms.service.app.response.Refund.RefundExchangeItemResponse;
//import com.oms.service.app.response.Refund.RequestRefundExchangeResponse;
//import com.oms.service.app.response.ResponsePage;
//import com.oms.service.domain.entities.Account.Admin;
//import com.oms.service.domain.entities.Account.User;
//import com.oms.service.domain.entities.Order.Order;
//import com.oms.service.domain.entities.Order.OrderItem;
//import com.oms.service.domain.entities.Refund.RefundExchangeItem;
//import com.oms.service.domain.entities.Refund.RequestRefundExchange;
//import com.oms.service.domain.enums.StateOrderItem;
//import com.oms.service.domain.enums.StateRefundExchange;
//import com.oms.service.domain.enums.TypeRefundExchange;
//import com.oms.service.domain.exceptions.ErrorMessageOm;
//import com.oms.service.domain.repositories.Order.OrderItemRepository;
//import com.oms.service.domain.repositories.Order.OrderRepository;
//import com.oms.service.domain.repositories.RequestRefundExchangeRepository;
//import com.oms.service.domain.repositories.UserRepository;
//import com.oms.service.domain.services.AdminService;
//import com.oms.service.domain.services.RequestRefundExchangeService;
//import com.oms.service.domain.services.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class RequestRefundExchangeImpl implements RequestRefundExchangeService {
//	private final UserRepository userRepository;
//	private final RequestRefundExchangeRepository requestRefundExchangeRepository;
//	private final ModelMapper modelMapper;
//	private final UserService userService;
//	private final OrderRepository orderRepository;
//	private final AdminService adminService;
//	private final OrderItemRepository orderItemRepository;
//	@Override
//	@Transactional
//	public RequestRefundExchangeResponse canculateRequestRefundExchange(Long id, RequestRefundExchangeDto requestRefundExchangeDto) {
//		User user= userService.getAuthenticatedUser();
//		Order order=orderRepository.findByIdWithOrderItems(id,user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
//
//		// check order item
//		checkOrderItem(order,requestRefundExchangeDto);
//
//
//		RequestRefundExchangeResponse requestRefundExchangeResponse = new RequestRefundExchangeResponse();
//		requestRefundExchangeResponse.setId(order.getId());
//		requestRefundExchangeResponse.setCreatedAt(order.getCreatedAt());
//		List<RefundExchangeItemResponse> refundExchangeItemResponses = new ArrayList<>();
//		for (RefundExchangeItemDto refundExchangeItemDto : requestRefundExchangeDto.getListRefundExchangeItem()) {
//
//			OrderItem orderItem = order.getListOrderItems().stream()
//					.filter(o -> o.getId().equals(refundExchangeItemDto.getOrderItemId()))
//					.findFirst()
//					.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_ITEM_NOT_FOUND.val()));
//
//			BigDecimal canFee_exchange = checkTimeAndCanculateFee(refundExchangeItemDto,orderItem);
//
//			RefundExchangeItemResponse refundExchangeItemResponse = createRefundExchangeItemResponse(orderItem, refundExchangeItemDto, canFee_exchange);
//			refundExchangeItemResponse.setProductInfo(orderItem.getProductInfo());
//			refundExchangeItemResponses.add(refundExchangeItemResponse);
//		}
//		requestRefundExchangeResponse.setListRefundExchangeItems(refundExchangeItemResponses);
//
//		return requestRefundExchangeResponse;
//	}
//	@Override
//	@Transactional
//	public RequestRefundExchangeResponse createRequestRefundExchange(Long id,RequestRefundExchangeDto requestRefundExchangeDto) {
//		if(requestRefundExchangeDto.getTotalFeeRefundExchange()==null) {
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.TOTAL_FEE_REFUND_EXCHANGE_NOT_FOUND.val());
//		}
//		User user= userService.getAuthenticatedUser();
//		Order order=orderRepository.findByIdWithOrderItems(id,user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
//
//		checkOrderItem(order,requestRefundExchangeDto);
//
//		RequestRefundExchange requestRefundExchange = new RequestRefundExchange();
//		requestRefundExchange.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//		requestRefundExchange.setDeleted(false);
//		requestRefundExchange.setStateRequestRefundExchange(StateRefundExchange.PENDING);
//
//		BigDecimal totalFeeRefundExchange = BigDecimal.ZERO;
//		for (RefundExchangeItemDto refundExchangeItemDto : requestRefundExchangeDto.getListRefundExchangeItem()) {
//			OrderItem orderItem = order.getListOrderItems().stream()
//					.filter(o -> o.getId().equals(refundExchangeItemDto.getOrderItemId()))
//					.findFirst()
//					.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_ITEM_NOT_FOUND.val()));
//
//
//			BigDecimal refundExchangeFee = checkTimeAndCanculateFee(refundExchangeItemDto,orderItem);
//			totalFeeRefundExchange=totalFeeRefundExchange.add(refundExchangeFee);
//			RefundExchangeItem refundExchangeItem = new RefundExchangeItem();
//			refundExchangeItem.setOrderItem(orderItem);
//			refundExchangeItem.setStateRefundExchange(StateRefundExchange.PENDING);
//			refundExchangeItem.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//			if(refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.REFUND)){
//				refundExchangeItem.setRefund_amount(refundExchangeFee);
//			}
//			else if(refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.EXCHANGE)){
//				refundExchangeItem.setFee_exchange(refundExchangeFee);
//			}
//
//			requestRefundExchange.addRefundExchange(refundExchangeItem);
//		}
//		//so sánh total gửi lên và total tính lại
//		if(totalFeeRefundExchange.compareTo(requestRefundExchangeDto.getTotalFeeRefundExchange())!=0){
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.INVALID_TOTAL_FEE_REFUND_EXCHANGE.val());
//		}
//		requestRefundExchange.setTotalRefundExchange(totalFeeRefundExchange);
//
//
//		order.addRequestRefundExchange(requestRefundExchange);
//		user.addRequestRefundExchange(requestRefundExchange);
//		userRepository.save(user);
//
//		return modelMapper.map(requestRefundExchange, RequestRefundExchangeResponse.class);
//	}
//
//	@Override
//	public List<RequestRefundExchangeResponse> getDetail(Long id) {
//		User user=userRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.USER_NOT_FOUND.val()));
//		List<RequestRefundExchange> listRequestRefundExchange=user.getListRequestRefundExchange();
//
//		modelMapper.typeMap(RequestRefundExchange.class,RequestRefundExchangeResponse.class).addMappings(mapper->{
//
//		});
//
//		return listRequestRefundExchange.stream().map(r->modelMapper.map(r,RequestRefundExchangeResponse.class)).toList();
//	}
//	public void checkOrderItem(Order order,RequestRefundExchangeDto requestRefundExchangeDto) {
//		List<OrderItem> orderItems = order.getListOrderItems();
//
//		List<Long> listOrderItemId = orderItems.stream()
//				.map(OrderItem::getId)
//				.toList();
//
//
//		List<Long> listOrderItemIdDto = requestRefundExchangeDto.getListRefundExchangeItem().stream()
//				.map(RefundExchangeItemDto::getOrderItemId)
//				.toList();
//
//		// Kiểm tra nếu một trong các OrderItem ID không tồn tại trong danh sách
//		if (!listOrderItemId.containsAll(listOrderItemIdDto)) {
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_ORDER_ITEM.val());
//		}
//
//		List<OrderItem> listOrderItem = orderItems.stream()
//				.filter(o -> listOrderItemIdDto.contains(o.getId()))
//				.toList();
//
//		// Kiểm tra xem có order Item nào không phải state COMPLETED,REFUNDED_FAILED không
//		for (OrderItem o : listOrderItem) {
//			if (!(o.getStateOrderItem().equals(StateOrderItem.COMPLETED) ||
//					o.getStateOrderItem().equals(StateOrderItem.REFUNDED_FAILED))) {
//				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_ITEM__NOT_COMPLETED_REFUNDED_FAILED.val());
//			}
//			// Kiểm tra xem có refund item nào đang trong trạng thái đang xử lý hoặc chờ refund/exchange không
//			boolean check = o.getListRefundExchangeItem().stream()
//					.anyMatch(item -> item.getStateRefundExchange().equals(StateRefundExchange.PENDING) || item.getStateRefundExchange().equals(StateRefundExchange.PROCESSING));
//			if (check) {
//				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ORDER_ITEM__HAS_REFUNNING_OR_EXCHANGING.val());
//			}
//
//		}
//	}
//	public BigDecimal checkTimeAndCanculateFee(RefundExchangeItemDto refundExchangeItemDto,OrderItem orderItem){
//		// Giả sử đây là thời gian shipping đến tận tay người dùng
//		Timestamp timestamp = Timestamp.valueOf("2024-06-9 10:30:00");
//		long monthsDifference = ChronoUnit.MONTHS.between( timestamp.toLocalDateTime(), LocalDateTime.now());
//
//		if(monthsDifference>MONTH_VALID_REFUND_OR_EXCHANGE){
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.INVALID_TIME_TO_EXCHANGE_OR_REFUND.val());
//		}
//		BigDecimal canFeeRefundExchange = BigDecimal.ZERO;
//
//		//giá trị bằng tổng tiền orderItem / số lượng orderItem * số lượng refund
//		BigDecimal priceOrderItem=orderItem.getTotalPrice().divide(BigDecimal.valueOf(orderItem.getQuantity()), RoundingMode.HALF_UP)
//				.multiply(BigDecimal.valueOf(refundExchangeItemDto.getQuantity()));
//
//		if(refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.REFUND)){
//			if (monthsDifference ==1) {
//				canFeeRefundExchange = priceOrderItem.multiply(BigDecimal.valueOf(PERCENT_REFUND_FIRST_MONTH));
//			}
//			else if(1<monthsDifference ){
//				canFeeRefundExchange = priceOrderItem.multiply(BigDecimal.valueOf(PERCENT_REFUND_OTHER_MONTH*monthsDifference));
//			}
//		}
//		else if(refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.EXCHANGE)){
//			if(1<monthsDifference){
//				canFeeRefundExchange = priceOrderItem.multiply(BigDecimal.valueOf(PERCENT_EXCHANGE_OTHER_MONTH*monthsDifference));
//			}
//		}
//		return canFeeRefundExchange;
//	}
//
//	private RefundExchangeItemResponse createRefundExchangeItemResponse(OrderItem orderItem, RefundExchangeItemDto refundExchangeItemDto, BigDecimal canFeeExchange) {
//		RefundExchangeItemResponse refundExchangeItemResponse = new RefundExchangeItemResponse();
//		refundExchangeItemResponse.setId(orderItem.getId());
//		refundExchangeItemResponse.setQuantity(orderItem.getQuantity());
//		refundExchangeItemResponse.setProductInfo(orderItem.getProductInfo());
//
//		if (refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.REFUND)) {
//			refundExchangeItemResponse.setTypeRefundExchange(TypeRefundExchange.REFUND);
//			refundExchangeItemResponse.setRefund_amount(canFeeExchange);
//		} else if (refundExchangeItemDto.getTypeRefundExchange().equals(TypeRefundExchange.EXCHANGE)) {
//			refundExchangeItemResponse.setTypeRefundExchange(TypeRefundExchange.EXCHANGE);
//			refundExchangeItemResponse.setFee_exchange(canFeeExchange);
//		}
//
//		return refundExchangeItemResponse;
//	}
//	@Override
//	public ResponsePage<RequestRefundExchange,RequestRefundExchangeResponse> getAllByUserId(FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable) {
//		User user= userService.getAuthenticatedUser();
//		Sort sort = Sort.by(Sort.Direction.fromString(String.valueOf(filterRefundExchangeDto.getSortedBy())), "created_at");
//		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//
//		Page<RequestRefundExchange> requestRefundExchangePage=requestRefundExchangeRepository.findAllByUserId(user.getId(),filterRefundExchangeDto.getStateRefundExchange(),sortedPageable);
//		return new ResponsePage<>(requestRefundExchangePage,RequestRefundExchangeResponse.class);
//	}
//	@Override
//	public List<RequestRefundExchangeResponse> getAll(FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable) {
//		Sort sort = Sort.by(Sort.Direction.fromString(String.valueOf(filterRefundExchangeDto.getSortedBy())), "created_at");
//		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//
//		Page<RequestRefundExchange> requestRefundExchangePage=requestRefundExchangeRepository.findAllByAdmin(filterRefundExchangeDto.getStateRefundExchange(),sortedPageable);
//		return requestRefundExchangePage.getContent().stream().map(r->modelMapper.map(r,RequestRefundExchangeResponse.class)).toList();
//	}
//	public RequestRefundExchangeResponse updateRefundExchangeItem(Long requestRefundExchangeId,Long refundExchangeItemId,Long orderItemId,UpdateRefundExchangeItemDto updateRefundExchangeItemDto) {
//		Admin admin=adminService.getAuthenticatedAdmin();
//		if(!(updateRefundExchangeItemDto.getDescriptionAdmin()!=null&&updateRefundExchangeItemDto.getStateRefundExchange()!=null)){
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.INVALID_UPDATE_REFUND_EXCHANGE.val());
//		}
//		RequestRefundExchange requestRefundExchange=requestRefundExchangeRepository.findById(requestRefundExchangeId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.REQUEST_REFUND_EXCHANGE_NOT_FOUND.val()));
//		RefundExchangeItem refundExchangeItem=requestRefundExchange.getListRefundExchangeItems().stream()
//				.filter(r->r.getId().equals(refundExchangeItemId)).findFirst().orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.REFUND_EXCHANGE_ITEM_NOT_FOUND.val()));
//		if(updateRefundExchangeItemDto.getStateRefundExchange().equals(StateRefundExchange.FAILED)){
//			if(updateRefundExchangeItemDto.getDescriptionAdmin()==null){
//				throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.DESCRIPTION_ADMIN_NOT_FOUND.val());
//			}
//			refundExchangeItem.setStateRefundExchange(StateRefundExchange.FAILED);
//			refundExchangeItem.setDescriptionAdmin(updateRefundExchangeItemDto.getDescriptionAdmin());
//			refundExchangeItem.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//
//			BigDecimal totalFeeRefundExchange=BigDecimal.ZERO;
//			if(refundExchangeItem.getTypeRefundExchange().equals(TypeRefundExchange.REFUND)){
//				totalFeeRefundExchange=requestRefundExchange.getTotalRefundExchange().subtract(refundExchangeItem.getRefund_amount());
//			}
//			if(refundExchangeItem.getStateRefundExchange().equals(TypeRefundExchange.EXCHANGE)){
//				totalFeeRefundExchange=requestRefundExchange.getTotalRefundExchange().subtract(refundExchangeItem.getFee_exchange());
//			}
//		}
//		requestRefundExchangeRepository.save(requestRefundExchange);
//		admin.addRequestRefundExchange(requestRefundExchange);
//
//		return modelMapper.map(requestRefundExchange,RequestRefundExchangeResponse.class);
//	}
//	@Override
//	public RequestRefundExchangeResponse updateRequestRefundExchange(Long requestRefundExchangeId, UpdateRequestRefundExchangeDto updateRequestRefundExchangeDto) {
//		Admin admin=adminService.getAuthenticatedAdmin();
//		RequestRefundExchange requestRefundExchange=requestRefundExchangeRepository.findById(requestRefundExchangeId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.REQUEST_REFUND_EXCHANGE_NOT_FOUND.val()));
//
//		requestRefundExchange.setStateRequestRefundExchange(updateRequestRefundExchangeDto.getStateRefundExchange());
//		requestRefundExchange.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
//		requestRefundExchangeRepository.save(requestRefundExchange);
//
//		return modelMapper.map(requestRefundExchange,RequestRefundExchangeResponse.class);
//	}
//}
