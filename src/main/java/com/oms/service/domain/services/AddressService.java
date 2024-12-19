package com.oms.service.domain.services;

import com.oms.service.app.dtos.AddressDto;
import com.oms.service.app.response.AddressResponse;

import java.util.List;

public interface AddressService {
	AddressResponse createAddress(AddressDto addressDto);

	AddressResponse updateAddress(Long id, AddressDto addressDto);

	AddressResponse deleteAddress(Long id);

	List<AddressResponse> getAllAddress();
}
