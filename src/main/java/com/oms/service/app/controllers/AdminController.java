package com.oms.service.app.controllers;

import com.oms.service.app.dtos.AdminDto;
import com.oms.service.app.response.AdminResponse;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.domain.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {
	private final AdminService adminService;
	@PostMapping
	public ApiResponse<AdminResponse> createAdmin(@RequestBody @Valid AdminDto adminDto){
		return new ApiResponse<>(HttpStatus.CREATED.value(),adminService.createdAdmin(adminDto));
	}
	@PostMapping("/login")
	public ApiResponse<AdminResponse> login(@RequestBody @Valid AdminDto adminDto){
		return new ApiResponse<>(HttpStatus.OK.value(),adminService.login(adminDto));
	}

	@PostMapping("/logout")
	public ApiResponse<AdminResponse> logout(){
		adminService.logout();
		return new ApiResponse<>(HttpStatus.NO_CONTENT.value());
	}
}
