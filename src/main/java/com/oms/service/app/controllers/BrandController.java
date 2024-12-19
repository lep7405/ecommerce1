package com.oms.service.app.controllers;

import com.oms.service.app.dtos.BrandDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.BrandResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Brand;
import com.oms.service.domain.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {
	private final BrandService brandService;
	@PostMapping
	public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandDto brandDto){
		return new ApiResponse<>(HttpStatus.OK.value(),brandService.createBrand(brandDto));
	}

	@PutMapping("/{id}")
	public ApiResponse<BrandResponse> updateBrand(@PathVariable Long id,@RequestBody @Valid BrandDto brandDto){
		return new ApiResponse<>(HttpStatus.OK.value(),brandService.updateBrand(id,brandDto));
	}

	@GetMapping()
	public ResponsePage<Brand, BrandResponse> getBrand(
			@RequestParam(value = "searchTerm", required = false) String searchTerm,
			Pageable pageable) {
		return brandService.getAllBrand(searchTerm, pageable);
	}
	@GetMapping("/category/{id}")
	public ApiResponse<List<BrandResponse>> getBrandByCategory(@PathVariable Long id,@RequestParam("pageable") Pageable pageable){
		return new ApiResponse<>(HttpStatus.OK.value(),brandService.getBrandByCategory(id,pageable));
	}

	@GetMapping("/all")
	public ApiResponse<List<BrandResponse>> getBrandById(){
		return new ApiResponse<>(HttpStatus.OK.value(),brandService.getAllBrandForCreateCategory());
	}
	@DeleteMapping("/{id}")
	public ApiResponse<BrandResponse> deleteBrand(@PathVariable Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),brandService.deleteBrand(id));
	}
}
