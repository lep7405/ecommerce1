package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Account.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin,Long>{
	SuperAdmin findByEmailEqualsIgnoreCase(String email);
}
