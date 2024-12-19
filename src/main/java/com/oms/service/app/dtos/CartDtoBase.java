package com.oms.service.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDtoBase {
	@NotNull(message = "Product ID must not be null.")
	private Long productId;
	@NotNull(message = "Product variant id must be not null")
	private Long productVariantId;
//	@NotNull(message = "Quantity must not be null.")
//	@Min(value=1, message = "Quantity must be at least 1.")
	private Integer quantity;
}
