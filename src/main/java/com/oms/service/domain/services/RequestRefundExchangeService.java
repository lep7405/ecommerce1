package com.oms.service.domain.services;

import com.oms.service.app.dtos.Refund.FilterRefundExchangeDto;
import com.oms.service.app.dtos.Refund.RequestRefundExchangeDto;
import com.oms.service.app.dtos.Refund.UpdateRefundExchangeItemDto;
import com.oms.service.app.dtos.Refund.UpdateRequestRefundExchangeDto;
import com.oms.service.app.response.Refund.RequestRefundExchangeResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Refund.RequestRefundExchange;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestRefundExchangeService {
	RequestRefundExchangeResponse createRequestRefundExchange(Long id, RequestRefundExchangeDto requestRefundExchangeDto);
	List<RequestRefundExchangeResponse> getDetail(Long id);
	ResponsePage<RequestRefundExchange,RequestRefundExchangeResponse> getAllByUserId(FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable);

	RequestRefundExchangeResponse canculateRequestRefundExchange(Long id, RequestRefundExchangeDto requestRefundExchangeDto);
	RequestRefundExchangeResponse updateRequestRefundExchange(Long requestRefundExchangeId, UpdateRequestRefundExchangeDto updateRequestRefundExchangeDto);
	RequestRefundExchangeResponse updateRefundExchangeItem(Long requestRefundExchangeId, Long refundExchangeItemId, Long orderItemId, UpdateRefundExchangeItemDto updateRefundExchangeItemDto);
	List<RequestRefundExchangeResponse> getAll(FilterRefundExchangeDto filterRefundExchangeDto, Pageable pageable);

}
