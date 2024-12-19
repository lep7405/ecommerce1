package com.oms.service.app.response.DisCount;

import com.oms.service.app.response.ProductResponse;
import com.oms.service.domain.enums.ProgramType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiscountResponse {
	private Long id;


	private List<ProductResponse> listProductResponses;
}
