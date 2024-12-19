package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
	@Query("select r from Review r where r.orderitem.productId = :productId")
	List<Review> findAllByProductId(@Param("productId") Long productId);
}
