package com.oms.service.domain.services;

import com.oms.service.app.dtos.AdminDto;
import com.oms.service.app.response.AdminResponse;
import com.oms.service.domain.entities.Account.Admin;

public interface AdminService {
	AdminResponse createdAdmin(AdminDto adminDto);
	AdminResponse login(AdminDto adminDto);

	void logout();

	Admin getAuthenticatedAdmin();

}
