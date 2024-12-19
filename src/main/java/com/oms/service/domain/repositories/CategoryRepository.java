package com.oms.service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.oms.service.domain.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findAllByNameContainingIgnoreCase(Pageable pageable, String name);

  Page<Category> findAllByParentCategoryIsNull(Pageable pageable);

  List<Category> findAllByParentCategoryId(Long parentId);


  Optional<Category> findByNameIgnoreCase(String name);
//  @Query("SELECT c FROM Category c " +
//          "LEFT JOIN FETCH c.listParameter " +
//          "LEFT JOIN FETCH c.listAttribute " +
//          "WHERE c.id = :id")
//  Optional<Category> findByIdWithDetails(@Param("id") Long id);


}
