package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.RoleDto;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.app.response.RoleResponse;
import com.oms.service.domain.entities.Role.Permission;
import com.oms.service.domain.entities.Role.Role;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Role.PermissionRepository;
import com.oms.service.domain.repositories.Role.RoleRepository;
import com.oms.service.domain.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;
	private final PermissionRepository permissionRepository;
	private final ModelMapper modelMapper;
	@Override
	@Transactional
	public RoleResponse createRole(RoleDto roleDto) {
		//check name
		checkDuplicateRoleName(roleDto.getName(), roleDto.getCode());

		//check xem tất cả id permission có tồn tại không
		Role newRole = new Role();
		newRole.setName(roleDto.getName());
		newRole.setCode(roleDto.getCode());
		newRole.setCreatedAt(LocalDateTime.now());
		newRole.setDeleted(false);
		if(roleDto.getListPermissionId()!=null && !roleDto.getListPermissionId().isEmpty()){
			List<Permission> permissionList = getPermissionsByIds(roleDto.getListPermissionId());
			checkDuplicateRolePermissions(roleDto.getListPermissionId());

			permissionList.forEach(permission -> newRole.getListPermissions().add(permission));
		}
		//
		roleRepository.save(newRole);
		//
		return modelMapper.map(newRole,RoleResponse.class);
	}

	@Override
	public RoleResponse updateRole(Long id, RoleDto roleDto) {
		Role role = roleRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ROLE_NOT_FOUND.val()));

		// Check name
		if (!role.getName().equalsIgnoreCase(roleDto.getName())) {
			checkDuplicateRoleName(roleDto.getName(), role.getCode());
		}
		//check permission id có tồn tàij không
		List<Permission> permissionList = getPermissionsByIds(roleDto.getListPermissionId());
		//check có role nào trùng list permission không
		checkDuplicateRolePermissions(roleDto.getListPermissionId());

		List<Permission> listPermission=role.getListPermissions();
		List<Long> listPermissionId=listPermission.stream().map(Permission::getId).toList();

		//líst các permission id cần xóa
		List<Long> listPermissionIdDelete=listPermissionId.stream().filter(permissionId->!roleDto.getListPermissionId().contains(permissionId)).toList();
		listPermissionIdDelete.forEach(permissionId->{
			Permission permission=listPermission.stream().filter(per->per.getId().equals(permissionId))
					.findFirst().orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PERMISSION_NOT_FOUND.val()));
			role.setListPermissions(role.getListPermissions().stream().filter(per->!per.getId().equals(permissionId)).toList());
		});

		for(Long permissionId:roleDto.getListPermissionId()){
			Permission permission=permissionList.stream().filter(per->per.getId().equals(permissionId))
					.findFirst().orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PERMISSION_NOT_FOUND.val()));
			role.setListPermissions(new ArrayList<>(role.getListPermissions()));
			role.getListPermissions().add(permission);
		}
		//
		role.setName(roleDto.getName());
		role.setUpdatedAt(LocalDateTime.now());
		roleRepository.save(role);

		// Map role to response
		return modelMapper.map(role,RoleResponse.class);
	}

	private void checkDuplicateRoleName(String roleName,String code) {
		Role existingRole = roleRepository.findByNameIgnoreCaseOrCode(roleName,code);
		if (existingRole != null) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_ROLE_NAME_OR_CODE.val());
		}
	}

	private List<Permission> getPermissionsByIds(List<Long> permissionIds) {
		List<Permission> permissionList = permissionRepository.findAllById(permissionIds);
		if (permissionList.size() != permissionIds.size()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PERMISSION_NOT_FOUND.val());
		}
		return permissionList;
	}


	private void checkDuplicateRolePermissions(List<Long> permissionIds) {
		List<Role> rolesWithSamePermissions = roleRepository.findRolesWithMatchingPermissions(permissionIds, (long) permissionIds.size());
		if (!rolesWithSamePermissions.isEmpty()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ROLE_EXIST_WITH_LIST_PERMISSION.val());
		}
	}

	@Override
	public RoleResponse getRole(Long id){
		Role role=roleRepository.findById(id).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ROLE_NOT_FOUND.val()));
		return modelMapper.map(role,RoleResponse.class);
	}

	public List<RoleResponse> getAllRole(Pageable pageable) {
		ResponsePage<Role, RoleResponse> responsePage = new ResponsePage<>();
		Page<Role> listRole = roleRepository.findAll(pageable);
		List<RoleResponse> roleResponseList=new ArrayList<>();
		for(Role role: listRole.getContent()){
			roleResponseList.add(modelMapper.map(role,RoleResponse.class));
		}
		return roleResponseList;
	}

	public RoleResponse deleteRole(Long id) {
		Role role=roleRepository.findById(id).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ROLE_NOT_FOUND.val()));
		role.setDeleted(true);
		role.setUpdatedAt(LocalDateTime.now());
		roleRepository.save(role);
		return modelMapper.map(role,RoleResponse.class);
	}
}
