package com.oms.service.app.controllers;

import com.oms.service.app.dtos.RoleDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.RoleResponse;
import com.oms.service.domain.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleControlelr {
	private final RoleService roleService;
	@PostMapping
	public ApiResponse<RoleResponse> createResponse(@RequestBody @Valid RoleDto roleDto){
		return new ApiResponse<>(HttpStatus.OK.value(),roleService.createRole(roleDto));
	}
}
