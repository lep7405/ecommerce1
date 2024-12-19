package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.BrandDto;
import com.oms.service.app.response.BrandResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Brand;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.BrandRepository;
import com.oms.service.domain.services.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {
	private final BrandRepository brandRepository;
	private final ModelMapper modelMapper;
	@Override
	public BrandResponse createBrand(BrandDto brandDto) {
		Optional<Brand> brandCheck=brandRepository.findByNameIgnoreCase(brandDto.getName());
		if(brandCheck.isPresent()) throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.BRAND_EXISTS);
		Brand brand=new Brand();
		brand.setName(brandDto.getName());
		brand.setUrl(brandDto.getUrl());
		brand.setDeleted(false);
		brand.setCreatedAt(LocalDateTime.now());
		brandRepository.save(brand);
		return modelMapper.map(brand, BrandResponse.class);
	}
	@Override
	public BrandResponse updateBrand(Long id, BrandDto brandDto) {
		long startTime = System.currentTimeMillis(); // Thời gian bắt đầu

		long findStartTime = System.currentTimeMillis(); // Bắt đầu tìm kiếm brand
		Brand brand = brandRepository.findById(id)
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.BRAND_NOT_FOUND));
		if(brand.getName()!=brandDto.getName()){
			Optional<Brand> brandCheck=brandRepository.findByNameIgnoreCase(brandDto.getName());
			if(brandCheck.isPresent()) throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.BRAND_EXISTS);
		}
		long findEndTime = System.currentTimeMillis(); // Kết thúc tìm kiếm
		System.out.println("Finding brand took: " + (findEndTime - findStartTime) + "ms");

		long updateStartTime = System.currentTimeMillis(); // Bắt đầu cập nhật giá trị
		brand.setName(brandDto.getName());
		brand.setUrl(brandDto.getUrl());
		long updateEndTime = System.currentTimeMillis(); // Kết thúc cập nhật giá trị
		System.out.println("Updating brand fields took: " + (updateEndTime - updateStartTime) + "ms");

		long saveStartTime = System.currentTimeMillis(); // Bắt đầu lưu vào DB
		brandRepository.save(brand);
		long saveEndTime = System.currentTimeMillis(); // Kết thúc lưu vào DB
		System.out.println("Saving brand to database took: " + (saveEndTime - saveStartTime) + "ms");

		long mapStartTime = System.currentTimeMillis(); // Bắt đầu map sang BrandResponse
		BrandResponse response = modelMapper.map(brand, BrandResponse.class);
		long mapEndTime = System.currentTimeMillis(); // Kết thúc map
		System.out.println("Mapping to BrandResponse took: " + (mapEndTime - mapStartTime) + "ms");

		long endTime = System.currentTimeMillis(); // Kết thúc tổng
		System.out.println("Total time taken for updateBrand: " + (endTime - startTime) + "ms");

		return response;
	}
	@Override
	public BrandResponse deleteBrand(Long id) {
		Brand brand = brandRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.BRAND_NOT_FOUND));
		brand.setDeleted(true);
		brandRepository.save(brand);
		return modelMapper.map(brand, BrandResponse.class);
	}

	@Override
	public ResponsePage<Brand, BrandResponse> getAllBrand(String searchTerm, Pageable pageable) {
		Page<Brand> brandPage = brandRepository.findAll(searchTerm,pageable);
		return new ResponsePage<>(brandPage, BrandResponse.class);
	}

	@Override
	public List<BrandResponse> getBrandByCategory(Long id, Pageable pageable) {
		Page<Brand> brandPage = brandRepository.findAllByCategoryId(id,pageable);
		return null;
	}

	@Override
	public List<BrandResponse> getAllBrandForCreateCategory() {
		List<Brand> brandList=brandRepository.findAll();
		List<BrandResponse> brandResponseList=new ArrayList<>();
		modelMapper.typeMap(Brand.class, BrandResponse.class).addMappings(mapper -> {
			mapper.skip(BrandResponse::setUrl);
		});

		for (Brand brand : brandList) {
			brandResponseList.add(modelMapper.map(brand, BrandResponse.class));
		}
		return brandResponseList;
	}

}
