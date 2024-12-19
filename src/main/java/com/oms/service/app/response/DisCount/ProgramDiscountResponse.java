package com.oms.service.app.response.DisCount;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgramDiscountResponse {
	private Long id;
	private String name;
	private String description;
	private String image;
	private ProgramDiscountType programDiscountType;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private ProgramType programType;

	private List<DiscountResponse> listDiscountResponse;
}
