package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Account.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
	Admin findByEmailEqualsIgnoreCase(String email);
}
