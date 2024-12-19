package com.oms.service.domain.services;

import com.oms.service.app.dtos.Refund.RefundExchangeItemDto;
import com.oms.service.app.response.Refund.RefundExchangeItemResponse;

public interface RefundExchangeItemService {
	RefundExchangeItemResponse update(Long id, RefundExchangeItemDto refundExchangeItemDto);


}
