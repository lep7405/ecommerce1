package com.oms.service.domain.services;

import com.oms.service.app.dtos.RoleDto;
import com.oms.service.app.response.RoleResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
	RoleResponse createRole(RoleDto roleDto);

	RoleResponse updateRole(Long id, RoleDto roleDto);

	RoleResponse getRole(Long id);

	List<RoleResponse> getAllRole(Pageable pageable);

	RoleResponse deleteRole(Long id);
}
