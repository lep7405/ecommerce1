package com.oms.service.domain.repositories.Address;

import com.oms.service.domain.entities.Address.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
	@Query("select a from Address a where a.user.id=:userId")
	Page<Address> findAllByUserId(Long userId, Pageable pageable);

	@Query("select a from Address a where a.user.id=:userId and a.id=:id")
	Address findByIdAndUserId(Long id, Long userId);
}
