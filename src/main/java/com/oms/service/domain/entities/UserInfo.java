package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {
	private Long userId;
	private String userName;
	private String email;
	private String phone;
	private String address;

	private String fullName;
	private String province;
	private String district;
	private String ward;
}
