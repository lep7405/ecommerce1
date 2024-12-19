package com.oms.service.app.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuperAdminResponse {
	private Long id;
	private String email;
	private String accessToken;
	private String refreshToken;
}
