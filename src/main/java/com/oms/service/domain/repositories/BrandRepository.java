package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Long> {
	Optional<Brand> findByNameIgnoreCase(String name);

	@Query("SELECT b FROM Brand b WHERE :searchTerm IS NULL OR b.name LIKE %:searchTerm%")
	Page<Brand> findAll(@Param("searchTerm") String searchTerm, Pageable pageable);


	@Query("select b from Brand b join b.listCategory c where c.id=:categoryId")
	Page<Brand> findAllByCategoryId(Long categoryId, Pageable pageable);
}
