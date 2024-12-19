package com.oms.service.domain.services.impl;

import com.oms.service.app.dtos.Refund.RefundExchangeItemDto;
import com.oms.service.app.response.Refund.RefundExchangeItemResponse;
import com.oms.service.domain.services.RefundExchangeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefundExchangeItemServiceImpl implements RefundExchangeItemService {

	@Override
	public RefundExchangeItemResponse update(Long id, RefundExchangeItemDto refundExchangeItemDto) {

		return null;
	}
}
