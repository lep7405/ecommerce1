package com.oms.service.domain.services;

import com.oms.service.app.dtos.Filter.FiltersDto;
import com.oms.service.app.response.AttributeResponse;
import com.oms.service.app.response.Filters.FilterResponse;
import com.oms.service.domain.entities.Filter.Filters;

import java.util.List;
import java.util.logging.Filter;

public interface FilterService {
	List<FilterResponse> createListFitlters(Long categoryId,List<FiltersDto> listFiltersDtos);
	List<FilterResponse> getFiltersByCategory(Long categoryId);

	List<AttributeResponse> getAllAttributeByCategory(Long id);
}
