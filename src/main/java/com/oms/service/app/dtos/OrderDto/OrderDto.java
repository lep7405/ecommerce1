package com.oms.service.app.dtos.OrderDto;

import com.oms.service.domain.entities.Payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
	private Long orderId;//trường hợp thanh toán thất bại thì cho thanh toán lại
	@NotEmpty(message = "listCartItemId must not be null or empty.")
	private List<Long> listCartItemId;

	@NotNull(message = "paymentMethodId must not be null.")
	private Long paymentMethodId;

	@NotNull
	private Long addressId;
}
