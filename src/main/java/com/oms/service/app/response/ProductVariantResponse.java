package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariantResponse {
	private Long id;
	private BigDecimal price;
	private Integer quantity;
	private String image;
	private List<AttributeResponse> listAttributes;

	private List<AttributeValueResponse> attributeValueResponses;
}
