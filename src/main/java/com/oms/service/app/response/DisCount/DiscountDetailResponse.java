package com.oms.service.app.response.DisCount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.app.response.CategoryResponse;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.RelDiscountProduct;
import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountDetailResponse {
	private Long id;
	private String name;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ProgramType programType;

	private DiscountType discountType;
	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
	private BigDecimal maxDiscountAmount;
	private BigDecimal minOrderAmount;
	private List<ProductDiscountResponse> listRelDiscountProduct;
	private List<CategoryResponse> listCategory;
}
