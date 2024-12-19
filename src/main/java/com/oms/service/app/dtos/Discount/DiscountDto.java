package com.oms.service.app.dtos.Discount;

import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import com.oms.service.domain.enums.PromotionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscountDto {
	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 225, message = "Name must be less than 225 characters")
	private String name;
	@NotBlank(message = "Code is required")
	@Size(min = 1, max = 225, message = "Code must be less than 225 characters")
	private String code;
	@NotNull(message = "Program type is required")
	private ProgramType programType;
	@NotNull(message = "End date is required")
	private Timestamp endDate;

	@NotNull(message = "Start date is required")
	private Timestamp startDate;

	private DiscountType discountType;

	private BigDecimal minimumAmount;
	private BigDecimal maximumAmount;
	private BigDecimal discountAmount;
	private BigDecimal discountPercentage;
	private String description;

	private List<Long> listVariantId;
	private List<Long> listProductId;//cho discount combo
	private List<Long> listProductIdGift;



}
