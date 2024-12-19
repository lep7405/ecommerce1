package com.oms.service.app.response.DiscountCombo1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.app.response.DiscountCombo.DiscountComboResponse;
import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgramDiscountCombo1Response {
	private Long id;
	private String name;
	private ProgramDiscountType programDiscountType;
	private ProgramType programType;
	private Timestamp startDate;
	private Timestamp endDate;

	private List<ProductResponeSimpleResponse> listProductMainResponse;
	private List<DiscountCombo1Response> listDiscountCombo1Response;
}
