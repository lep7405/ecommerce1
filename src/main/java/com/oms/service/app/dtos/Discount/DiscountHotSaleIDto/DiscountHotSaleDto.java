package com.oms.service.app.dtos.Discount.DiscountHotSaleIDto;

import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import com.oms.service.domain.enums.PromotionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscountHotSaleDto {

	private DiscountType discountType;
	private BigDecimal minimumAmount;
	private BigDecimal maximumAmount;

	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;

	private Long categoryId;

	private Long productId;
	private Long productVariantId;
	private Integer quantityLimit;
	private Integer purchaseLimit;

}
