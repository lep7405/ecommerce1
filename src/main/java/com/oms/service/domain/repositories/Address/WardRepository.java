package com.oms.service.domain.repositories.Address;

import com.oms.service.domain.entities.Address.Ward;
import nonapi.io.github.classgraph.utils.VersionFinder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WardRepository extends JpaRepository<Ward,Long> {
	List<Ward> findByDistrictCode(String code);
	Optional<Ward> findByCode(String code);
}
