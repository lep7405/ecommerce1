package com.oms.service.app.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserDto {
	@NotBlank(message = "Email is required")
	private String email;

	@NotNull(message = "Password is required")
	private String password;


}
