package com.oms.service.app.dtos;

import com.oms.service.app.dtos.Product.AttributeDto1;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class ProductVariantDto {
	private Long id;
	@NotNull(message = "Product Variant price must not be null.")
	@Min(value = 1, message = "price must be greater than or equal to 1")
	private BigDecimal price;
	@NotNull(message = "quantity is required")
	@Min(value = 1, message = "quantity must be greater than or equal to 1")
	private Integer quantity;
	@NotNull(message = "image must not be null.")
	private String image;
	private List<AttributeDto1> listAttributeDto;
}
