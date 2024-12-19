package com.oms.service.app.response.Address;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WardResponse {
	private String code;
	private String name;
}
