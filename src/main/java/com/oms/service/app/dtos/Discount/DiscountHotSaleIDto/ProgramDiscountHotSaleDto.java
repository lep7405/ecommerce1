package com.oms.service.app.dtos.Discount.DiscountHotSaleIDto;

import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProgramDiscountHotSaleDto {
	@NotBlank(message = "Name is required")
	@Size(min = 1, max = 225, message = "Name must be less than 225 characters")
	private String name;
	@NotNull(message = "Program type is required")
	private ProgramDiscountType programDiscountType;
	@NotNull(message = "Start date is required")

	@NotNull(message = "Program type is required")
	private ProgramType programType;

	private Timestamp startDate;
	@NotNull(message = "End date is required")
	private Timestamp endDate;
	@NotEmpty
	private List<DiscountHotSaleDto> listDiscountHotSaleDto;
}
