package com.oms.service.domain.repositories.Role;

import com.oms.service.domain.entities.Role.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
	Permission findByNameIgnoreCase(String name);
}
