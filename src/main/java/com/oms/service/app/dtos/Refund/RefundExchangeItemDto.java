package com.oms.service.app.dtos.Refund;

import com.oms.service.domain.enums.TypeRefundExchange;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class RefundExchangeItemDto {
	@NotNull(message = "orderItemId must not be null.")
	private Long orderItemId;
	@NotNull(message = "quantity must not be null.")
	private Integer quantity;
	@NotNull(message = "description must not be null.")
	private String description;
	@NotNull(message = "images must not be null.")
	private List<String> images;

	@NotNull(message = "typeRefundExchange must not be null.")
	private TypeRefundExchange typeRefundExchange;

}
