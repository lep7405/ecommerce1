package com.oms.service.app.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
	@NotNull
	private String name;
	@NotNull
	private String code;
	private List<Long> listPermissionId;
}
