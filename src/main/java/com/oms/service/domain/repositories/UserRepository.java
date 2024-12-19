package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

	User findByEmailEqualsIgnoreCase(String email);


}

