package com.oms.service.domain.repositories.Address;

import com.oms.service.domain.entities.Address.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District,Long> {
	List<District> findByProvinceCode(String code);

	Optional<District> findByCode(String code);
}
