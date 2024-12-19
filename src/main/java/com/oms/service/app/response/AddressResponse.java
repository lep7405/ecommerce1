package com.oms.service.app.response;

import com.oms.service.domain.enums.TypeAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class AddressResponse {
	private Long id;

	private String addressDetail;
	private String phoneNumber;
	private String fullName;
	private TypeAddress typeAddress;
	private Boolean isDefault;
	private String district;
	private String province;
	private String ward;
}
