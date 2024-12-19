package com.oms.service.app.controllers;

import com.oms.service.app.dtos.SuperAdminDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.SuperAdminResponse;
import com.oms.service.domain.entities.Account.SuperAdmin;
import com.oms.service.domain.repositories.SuperAdminRepository;
import com.oms.service.domain.services.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/super_admin")
public class SuperAdminController {
	private final SuperAdminService superAdminService;
	private final SuperAdminRepository superAdminRepository;

	@PostMapping()
	public ApiResponse<SuperAdminResponse> createSuperAdmin(@RequestBody @Valid SuperAdminDto superAdminDto){
		return new ApiResponse<>(HttpStatus.OK.value(),superAdminService.createSuperAdmin(superAdminDto));
	}
	@GetMapping("/{id}")
	public SuperAdmin GetSuperAdmin(@PathVariable Long id){
		return superAdminRepository.getById(id);
	}
	@PostMapping("/login")
	public ApiResponse<SuperAdminResponse> loginSuperAdmin(@RequestBody @Valid SuperAdminDto superAdminDto){
		return new ApiResponse<>(HttpStatus.OK.value(),superAdminService.loginSuperAdmin(superAdminDto));
	}
	@PostMapping("/logout")
	public ApiResponse<SuperAdminResponse> logoutSuperAdmin(){
		superAdminService.logout();
		return new ApiResponse<>(HttpStatus.NO_CONTENT.value());
	}
}
