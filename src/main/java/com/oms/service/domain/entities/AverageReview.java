package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class AverageReview {
	@Builder.Default
	private Integer numberOf5stars = 0;
	@Builder.Default
	private Integer numberOf4stars = 0;
	@Builder.Default
	private Integer numberOf3stars = 0;
	@Builder.Default
	private Integer numberOf2stars = 0;
	@Builder.Default
	private Integer numberOf1stars = 0;
}
