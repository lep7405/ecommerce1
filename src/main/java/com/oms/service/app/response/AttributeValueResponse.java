package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.entities.AttValue;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeValueResponse {
	private Long id;
	private Long attributeId;
	private String attValueString;
}
