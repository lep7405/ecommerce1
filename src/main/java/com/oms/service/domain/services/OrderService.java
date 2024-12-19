package com.oms.service.domain.services;

import com.oms.service.app.dtos.OrderDto.OrderDto;
import com.oms.service.app.dtos.OrderDto.OrderFilterDto;
import com.oms.service.app.dtos.OrderDto.OrderUpdateDto;
import com.oms.service.app.response.Order.OrderResponse;
import com.oms.service.app.response.Order.ProductSaleResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Order.Order;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

public interface OrderService {
	OrderResponse createOrderCart(Long id, OrderDto orderCartDto, HttpServletRequest request);
	ResponsePage<Order, OrderResponse> getAllOrdersByUser(Long userId, OrderFilterDto orderFilterDto, Pageable pageable);
	ResponsePage<Order,OrderResponse> getAllOrders(OrderFilterDto orderFilterDto, Pageable pageable);
	OrderResponse updateOrder(Long orderId, OrderUpdateDto orderUpdateDto);

	OrderResponse getOrderDetail(Long id);
	OrderResponse deleteOrder(Long id);
	void deleteAllOrdersByUser(Long id);

	ProductSaleResponse getSaleProduct(Long id, OrderFilterDto orderFilterDto);
}
