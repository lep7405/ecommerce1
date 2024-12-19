package com.oms.service.app.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoleResponse {
	private String name;
	private String code;
	private List<PermissionResponse> listPermissions;
}
