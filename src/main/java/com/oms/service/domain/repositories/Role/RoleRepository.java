package com.oms.service.domain.repositories.Role;

import com.oms.service.domain.entities.Role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
	//	@Query("SELECT r FROM Role r WHERE LOWER(r.name) = LOWER(:name) OR r.code = :code\n")
	Role findByNameIgnoreCaseOrCode(String name,String code);
	Role findByNameIgnoreCase(String name);
	@Query(value = """
    SELECT DISTINCT r.*
    FROM role r
    WHERE r.id IN (
        SELECT rp.role_id
        FROM rel_role_permission rp
        WHERE rp.permission_id IN :permissionIds
        GROUP BY rp.role_id
        HAVING COUNT(DISTINCT rp.permission_id) = :permissionCount
    )
""", nativeQuery = true)
	List<Role> findRolesWithMatchingPermissions(
			@Param("permissionIds") List<Long> permissionIds,
			@Param("permissionCount") Long permissionCount
	);



}
