package com.oms.service.app.dtos.Discount.DiscountGift;

import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class DiscountGiftDto {

	@NotNull(message = "Program type is required")
	private ProgramType programType;

//	private Long productMainId;
	private List<Long> listProductId;
	private List<Long> listProductMainId;
}
