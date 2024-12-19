package com.oms.service.app.dtos.Discount.DiscountCombo;

import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscountComboDto {
	@NotEmpty
	private Long productId;
	@NotNull
	private DiscountType discountType;
	private BigDecimal minimumAmount;
	private BigDecimal maximumAmount;

	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
	@NotNull
	private Integer quantityLimit;
	@NotNull
	private Integer purchaseLimit;
}
