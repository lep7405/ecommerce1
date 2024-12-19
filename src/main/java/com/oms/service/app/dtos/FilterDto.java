package com.oms.service.app.dtos;

import com.oms.service.domain.enums.StateProduct;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FilterDto {
	private String name;
	private Long categoryId;
	private Long typeProductId;
	private Long brandId;
	private StateProduct stateProduct;

	private BigDecimal minPrice;

	private BigDecimal maxPrice;

	List<Long> listFilterItemId;
}
