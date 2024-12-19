package com.oms.service.app.controllers;

import com.oms.service.app.dtos.OrderDto.OrderDto;
import com.oms.service.app.dtos.OrderDto.OrderFilterDto;
import com.oms.service.app.dtos.OrderDto.OrderUpdateDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.Order.OrderResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {
	private final OrderService orderService;

	@PostMapping("/{id}")
	public ApiResponse<OrderResponse> createOrderCart(@PathVariable Long id, @RequestBody @Valid OrderDto orderCartDto, HttpServletRequest request)  {
		OrderResponse orderResponse = orderService.createOrderCart(id,orderCartDto,request);
		return new ApiResponse<>(HttpStatus.OK.value(),orderResponse);
	}
	@PutMapping("/{id}")
	public OrderResponse updateOrder(@PathVariable Long id, @RequestBody OrderUpdateDto orderUpdateDto)  {
		return orderService.updateOrder(id, orderUpdateDto);
	}

	@GetMapping("/user/{id}")
	public ResponsePage<Order, OrderResponse> getAllOrderByUser(@PathVariable Long id, @ModelAttribute OrderFilterDto orderFilterDto, Pageable pageable)  {
		return orderService.getAllOrdersByUser(id,orderFilterDto,pageable);
	}

	@GetMapping()
	public ResponsePage<Order, OrderResponse> getAllOrder(@ModelAttribute OrderFilterDto orderFilterDto, Pageable pageable)  {
		return orderService.getAllOrders(orderFilterDto,pageable);
	}

	@GetMapping("/{id}")
	public ApiResponse<OrderResponse> getOrderDetail(@PathVariable Long id)  {
		return new ApiResponse<>(HttpStatus.OK.value(),orderService.getOrderDetail(id));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<OrderResponse> deleteOrder(@PathVariable Long id)  {
		return new ApiResponse<>(HttpStatus.OK.value(),orderService.deleteOrder(id));
	}

	@DeleteMapping("/_bulk/user/{id}")
	public ApiResponse<String> deleteAllOrdersByUser(@PathVariable Long id) {
		orderService.deleteAllOrdersByUser(id);
		return new ApiResponse<>(HttpStatus.OK.value());
	}

}
