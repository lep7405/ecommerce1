package com.oms.service.domain.entities.Order;

import com.oms.service.app.response.Attribute.AttributeResponseBase;
import com.oms.service.app.response.AttributeValueResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantInfo {
	private List<AttributeValueResponse> listAttributeValue;
	private BigDecimal price;
}
