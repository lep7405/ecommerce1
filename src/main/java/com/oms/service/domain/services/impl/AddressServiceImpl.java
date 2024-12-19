package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.AddressDto;
import com.oms.service.app.response.AddressResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Address.Address;
import com.oms.service.domain.entities.Address.District;
import com.oms.service.domain.entities.Address.Province;
import com.oms.service.domain.entities.Address.Ward;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Address.AddressRepository;
import com.oms.service.domain.repositories.Address.DistrictRepository;
import com.oms.service.domain.repositories.Address.ProvinceRepository;
import com.oms.service.domain.repositories.Address.WardRepository;
import com.oms.service.domain.repositories.UserRepository;
import com.oms.service.domain.services.AddressService;
import com.oms.service.domain.services.UserService;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	private final UserService userService;
	private final ProvinceRepository provinceRepository;
	private final DistrictRepository districtRepository;
	private final WardRepository wardRepository;
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;

	private final ModelMapper modelMapper;

	@Override
	public AddressResponse createAddress(AddressDto addressDto) {
		User user = userService.getAuthenticatedUser();

		// Tìm tỉnh, quận, xã (ward)
		Province province = provinceRepository.findByCode(addressDto.getProvinceCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PROVINCE_NOT_FOUND.val()));
		District district = districtRepository.findByCode(addressDto.getDistrictCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISTRICT_NOT_FOUND.val()));
		Ward ward = wardRepository.findByCode(addressDto.getWardCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.WARD_NOT_FOUND.val()));

		// Kiểm tra sự hợp lệ của tỉnh, quận, xã
		if (!province.getListDistrict().contains(district) || !district.getListWard().contains(ward)) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ADDRESS.val());
		}

		// Đặt lại địa chỉ mặc định nếu cần
		if (addressDto.getIsDefault()) {
			user.getListAddress().stream()
					.filter(Address::getIsDefault)
					.findFirst()
					.ifPresent(existingAddress -> {
						existingAddress.setIsDefault(false);
						existingAddress.setUpdatedAt(LocalDateTime.now());
					});
		}

		// Tạo và lưu địa chỉ mới
		Address address = new Address();
		address.setIsDefault(addressDto.getIsDefault());
		address.setAddressDetail(addressDto.getAddressDetail());
		address.setPhoneNumber(addressDto.getPhoneNumber());
		address.setTypeAddress(addressDto.getTypeAddress());
		address.setFullName(addressDto.getFullName());

		address.setProvince(province);
		address.setDistrict(district);
		address.setWard(ward);

		address.setCreatedAt(LocalDateTime.now());
		address.setUpdatedAt(LocalDateTime.now());
		address.setDeleted(false);

		user.addAddress(address);
		userRepository.save(user);

		return modelMapper.map(address, AddressResponse.class);
	}

	public AddressResponse updateAddress(Long id, AddressDto addressDto) {
		Address address = addressRepository.findById(id)
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val()));

		User user = userService.getAuthenticatedUser();

		// Tìm tỉnh, quận, xã (ward)
		Province province = provinceRepository.findByCode(addressDto.getProvinceCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PROVINCE_NOT_FOUND.val()));
		District district = districtRepository.findByCode(addressDto.getDistrictCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISTRICT_NOT_FOUND.val()));
		Ward ward = wardRepository.findByCode(addressDto.getWardCode())
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.WARD_NOT_FOUND.val()));

		// Kiểm tra sự hợp lệ của tỉnh, quận, xã
		if (!province.getListDistrict().contains(district) || !district.getListWard().contains(ward)) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ADDRESS.val());
		}

		// check địa chỉ xem có thuộc user
		if (user.getListAddress().stream().noneMatch(a -> a.getId().equals(id))) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val());
		}

		// Đặt lại địa chỉ mặc định nếu cần
		if (addressDto.getIsDefault() && !address.getIsDefault()) {
			user.getListAddress().stream()
					.filter(Address::getIsDefault)
					.findFirst()
					.ifPresent(existingAddress -> {
						existingAddress.setIsDefault(false);
						existingAddress.setUpdatedAt(LocalDateTime.now());
					});
		}

		// Cập nhật thông tin địa chỉ
		address.setAddressDetail(addressDto.getAddressDetail());
		address.setIsDefault(addressDto.getIsDefault());
		address.setProvince(province);
		address.setDistrict(district);
		address.setWard(ward);
		address.setUpdatedAt(LocalDateTime.now());

		addressRepository.save(address);

		return modelMapper.map(address, AddressResponse.class);
	}



	@Override
	public List<AddressResponse> getAllAddress() {
		User user=userService.getAuthenticatedUser();
		List<Address> listAddress=user.getListAddress();
		List<AddressResponse> listAddressReponse=new ArrayList<>();
		for(Address a:listAddress) {
			AddressResponse addressResponse=new AddressResponse();
			addressResponse.setPhoneNumber(a.getPhoneNumber());
			addressResponse.setId(a.getId());
			addressResponse.setTypeAddress(a.getTypeAddress());
			addressResponse.setFullName(a.getFullName());
			addressResponse.setIsDefault(a.getIsDefault());
			addressResponse.setAddressDetail(a.getAddressDetail());
			addressResponse.setFullName(a.getFullName());

			Province province=provinceRepository.findByCode(a.getProvince().getCode()).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PROVINCE_NOT_FOUND));
			District district=districtRepository.findByCode(a.getDistrict().getCode()).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.DISTRICT_NOT_FOUND));
			Ward ward=wardRepository.findByCode(a.getWard().getCode()).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.WARD_NOT_FOUND));

			addressResponse.setProvince(province.getName());
			addressResponse.setDistrict(district.getName());
			addressResponse.setWard(ward.getName());
			listAddressReponse.add(addressResponse);
		}
		return listAddressReponse;
	}

	public AddressResponse deleteAddress(Long id){
		Address address=addressRepository.findById(id).orElseThrow(()-> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val()));

		User user=userService.getAuthenticatedUser();
		if(user.getListAddress().stream().noneMatch(a->a.getId().equals(id))) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val());
		}
		address.setDeleted(true);
		address.setUpdatedAt(LocalDateTime.now());
		addressRepository.save(address);
		return modelMapper.map(address, AddressResponse.class);
	}

}
