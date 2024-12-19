package com.oms.service.app.response.Cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.app.response.AttributeValueResponse;
import com.oms.service.domain.entities.Product.Images;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

	private Long cartItemId;
	private Long productId;
	private Long productVariantId;
	private Long max1Buy;

	private Boolean isActive;
	private String productName;
	private String imageUrl;
	private Integer quantity;
	private BigDecimal price;

	private List<AttributeValueResponse> listAttributes;
}
