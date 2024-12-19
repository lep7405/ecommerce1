package com.oms.service.app.response.DiscountHotSale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgramDiscountHotSaleResponse {
	private Long id;
	private String name;
	private ProgramDiscountType programDiscountType;
	private ProgramType programType;
	private Timestamp startDate;
	private Timestamp endDate;

	private List<DiscountHotSaleResponse> listDiscountHotSaleResponse;
}
