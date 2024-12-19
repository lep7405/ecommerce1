package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterResponse {
	private Long id;
	private String name;
	private Integer groupIndex;
	List<AttributeResponse> listAttributes;
}
