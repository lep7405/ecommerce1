package com.oms.service.app.controllers;

import com.oms.service.app.dtos.Filter.FiltersDto;
import com.oms.service.app.dtos.FilterDto;
import com.oms.service.app.response.AttributeResponse;
import com.oms.service.app.response.Filters.FilterResponse;
import com.oms.service.domain.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/filters")
public class FilterController {
	private final FilterService filterService;

	@PostMapping("/category/{categoryId}")
	public List<FilterResponse> createListFilters( @PathVariable Long categoryId,@RequestBody @Valid List<FiltersDto> listFiltersDtos) {
		return filterService.createListFitlters(categoryId,listFiltersDtos);
	}

	@GetMapping("/category/{categoryId}")
	public List<FilterResponse> getListFilters(@PathVariable("categoryId") Long categoryId) {
		return filterService.getFiltersByCategory(categoryId);
	}

	@GetMapping("/category/{id}/attribute")
	public List<AttributeResponse> getAllAttributeByCategory(@PathVariable("id") Long id) {
		return filterService.getAllAttributeByCategory(id);
	}

}
