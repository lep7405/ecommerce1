package com.oms.service.domain.services;

import com.oms.service.app.dtos.CartDtoBase;
import com.oms.service.app.response.Cart.CartItemResponse;
import com.oms.service.app.response.Cart.CartResponse;

public interface CartService {

	CartItemResponse addItemToCart(CartDtoBase cartDtoBase);
	CartItemResponse updateCartItem(Long cartItemId,Integer quantity);
	CartResponse getCart();
	CartItemResponse removeCartItem( Long cartItemId);
	void removeAllCartItem();
}
