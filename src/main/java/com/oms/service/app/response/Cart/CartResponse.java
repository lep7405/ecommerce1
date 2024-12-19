package com.oms.service.app.response.Cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
	private Long id;
	private List<CartItemResponse> listCartItems;
}
