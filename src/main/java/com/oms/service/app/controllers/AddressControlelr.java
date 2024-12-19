package com.oms.service.app.controllers;

import com.oms.service.app.dtos.AddressDto;
import com.oms.service.app.response.Address.DistrictResponse;
import com.oms.service.app.response.Address.ProvinceResponse;
import com.oms.service.app.response.Address.WardResponse;
import com.oms.service.app.response.AddressResponse;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.domain.entities.Address.District;
import com.oms.service.domain.entities.Address.Province;
import com.oms.service.domain.entities.Address.Ward;
import com.oms.service.domain.repositories.Address.DistrictRepository;
import com.oms.service.domain.repositories.Address.ProvinceRepository;
import com.oms.service.domain.repositories.Address.WardRepository;
import com.oms.service.domain.services.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
@Slf4j
public class AddressControlelr {
	private final ProvinceRepository provinceRepository;
	private final DistrictRepository districtRepository;
	private final WardRepository wardRepository;
	private final ModelMapper modelMapper;

	private final AddressService addressService;
	@GetMapping("/allProvince")
	public ApiResponse<List<ProvinceResponse>> getAllProvince() {
		List<Province> listProvince = provinceRepository.findAll();
		List<ProvinceResponse> listProvinceResponse = new ArrayList<>();
		for(Province province : listProvince) {
			listProvinceResponse.add(modelMapper.map(province, ProvinceResponse.class));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),listProvinceResponse);
	}
	@GetMapping("/province/{code}/getDistrict")
	public ApiResponse<List<DistrictResponse>> getAllDistrict(@PathVariable("code") String code) {
		List<District> listDistrict = districtRepository.findByProvinceCode(code);
		List<DistrictResponse> listDistrcitResponse = new ArrayList<>();
		for(District district : listDistrict) {
			listDistrcitResponse.add(modelMapper.map(district, DistrictResponse.class));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),listDistrcitResponse);
	}
	@GetMapping("/district/{code}/getWard")
	public ApiResponse<List<WardResponse>> getAllWard(@PathVariable("code") String code) {
		List<Ward> listWard = wardRepository.findByDistrictCode(code);
		List<WardResponse> listWardResponse = new ArrayList<>();
		for(Ward ward : listWard) {
			listWardResponse.add(modelMapper.map(ward, WardResponse.class));
		}
		return new ApiResponse<>(HttpStatus.OK.value(),listWardResponse);
	}

	@GetMapping()
	public ApiResponse<List<AddressResponse>> getAllAddressUser() {
		return new ApiResponse<>(HttpStatus.OK.value(),addressService.getAllAddress());
	}

	@PostMapping()
	public ApiResponse<AddressResponse> createAddress(@RequestBody @Valid AddressDto addressDto) {
		return new ApiResponse<>(HttpStatus.OK.value(),addressService.createAddress(addressDto));
	}

	@PutMapping("/{id}")
	public ApiResponse<AddressResponse> updateAddress(@PathVariable("id") Long id, @RequestBody @Valid AddressDto addressDto) {
		return new ApiResponse<>(HttpStatus.OK.value(),addressService.updateAddress(id, addressDto));
	}
	
	@DeleteMapping("/{id}")
	public ApiResponse<AddressResponse> deleteAddress(@PathVariable("id") Long id) {
		return new ApiResponse<>(HttpStatus.OK.value(),addressService.deleteAddress(id));
	}
}
