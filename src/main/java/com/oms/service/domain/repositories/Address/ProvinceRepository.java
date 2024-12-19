package com.oms.service.domain.repositories.Address;

import com.oms.service.domain.entities.Address.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province,Long>{
	Optional<Province> findByCode(String code);
}
