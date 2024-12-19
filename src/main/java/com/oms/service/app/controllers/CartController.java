package com.oms.service.app.controllers;

import com.oms.service.app.dtos.CartDtoBase;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.Cart.CartItemResponse;
import com.oms.service.app.response.Cart.CartResponse;
import com.oms.service.domain.services.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
@Slf4j
public class CartController {
	private final CartService cartService;
	@PostMapping()
	public ApiResponse<CartItemResponse> addItemToCart(@RequestBody @Valid CartDtoBase cartDtoBase) {
		return new ApiResponse<>(HttpStatus.OK.value(),cartService.addItemToCart(cartDtoBase));
	}
	@PutMapping("/cartItems/{cartItemId}")
	public ApiResponse<CartItemResponse> updateCartItem(@PathVariable Long cartItemId, @RequestBody @NotNull(message = "Quantity must not be null.")
														@Min(value = 1, message = "Quantity must be at least 1.") Integer quantity) {
		return new ApiResponse<>(HttpStatus.OK.value(), cartService.updateCartItem(cartItemId,quantity));
	}
	@GetMapping("")
	public ApiResponse<CartResponse> getCart() {
		return new ApiResponse<>(HttpStatus.OK.value(),cartService.getCart());
	}
	@DeleteMapping("/cartItems/{cartItemId}")
	public ApiResponse<CartItemResponse> removeCartItem(@PathVariable Long cartItemId) {
		return new ApiResponse<>(HttpStatus.OK.value(), cartService.removeCartItem(cartItemId));
	}
	@DeleteMapping()
	public ApiResponse<String> removeAllCartItem() {
		cartService.removeAllCartItem();
		return new ApiResponse<>(HttpStatus.NO_CONTENT.value());
	}
}
