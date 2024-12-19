package com.oms.service.app.dtos;

import com.oms.service.domain.enums.TypeAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
	@NotNull
	private String addressDetail;
	@NotNull
	private String phoneNumber;
	@NotNull
	private String fullName;
	@NotNull
	private TypeAddress typeAddress;
	@NotNull
	private Boolean isDefault;
	@NotNull
	private String districtCode;
	@NotNull
	private String provinceCode;
	@NotNull
	private String wardCode;
}
