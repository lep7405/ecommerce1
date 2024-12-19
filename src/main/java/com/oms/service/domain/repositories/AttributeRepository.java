package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Product.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    Optional<Attribute> findByNameIgnoreCase(String name);

    @Query("SELECT a FROM Attribute a WHERE a.name = :name AND a.category.id = :categoryId")
    Optional<Attribute> findByNameIgnoreCaseAndCategory(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Query("SELECT a FROM Attribute a WHERE a.parameter.category.id = :categoryId and a.id in (:listAttributeId)")
    List<Attribute> findAllByCategory(@Param("listAttributeId") List<Long> listAttributeId,@Param("categoryId") Long categoryId);
}
