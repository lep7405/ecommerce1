package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<Parameter,Long> {
    Optional<Parameter> findByName(String name);
}
