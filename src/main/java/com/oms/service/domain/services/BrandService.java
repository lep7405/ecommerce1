package com.oms.service.domain.services;

import com.oms.service.app.dtos.BrandDto;
import com.oms.service.app.response.BrandResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Brand;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
	BrandResponse createBrand(BrandDto brandDto);
	BrandResponse updateBrand(Long id,BrandDto brandDto);
	BrandResponse deleteBrand(Long id);

	ResponsePage<Brand,BrandResponse> getAllBrand(String searchTerm,Pageable pageable);
	List<BrandResponse> getBrandByCategory(Long id,Pageable pageable);

	List<BrandResponse> getAllBrandForCreateCategory();
}
