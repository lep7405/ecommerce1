package com.oms.service.domain.services;

import com.oms.service.app.dtos.SuperAdminDto;
import com.oms.service.app.response.SuperAdminResponse;
import com.oms.service.domain.entities.Account.SuperAdmin;

public interface SuperAdminService {
	SuperAdminResponse createSuperAdmin(SuperAdminDto superAdminDto);
	SuperAdminResponse loginSuperAdmin(SuperAdminDto superAdminDto);
	void logout();
	SuperAdmin getAuthenticatedSuperAdmin();
}
