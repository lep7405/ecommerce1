package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.enums.CommitmentEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Commitment {
	@NotNull
	@Enumerated
	private CommitmentEnum type;
	@NotNull
	private String inputData;
}
