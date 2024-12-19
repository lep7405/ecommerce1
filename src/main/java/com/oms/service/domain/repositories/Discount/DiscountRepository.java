package com.oms.service.domain.repositories.Discount;

import com.oms.service.domain.entities.Discount.Discount;
import com.oms.service.domain.enums.ProgramType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface DiscountRepository extends JpaRepository<Discount,Long> {
//	@Query("SELECT d FROM Discount d " +
//			"JOIN d.programDiscount pd " +
//			"WHERE (:programType IS NULL OR d.programType = :programType) " +
//			"AND (LOWER(pd.name) LIKE LOWER(CONCAT('%', COALESCE(CAST(:name AS text), ''), '%'))) " +
//			"AND (COALESCE(:startDate, pd.startDate) = pd.startDate OR pd.startDate >= :startDate) " +
//			"AND (COALESCE(:endDate, pd.endDate) = pd.endDate OR pd.endDate <= :endDate)")
//	Page<Discount> findAll(
//			@Param("programType") String programType,
//			@Param("name") String name,
//			@Param("startDate") LocalDateTime startDate,
//			@Param("endDate") LocalDateTime endDate,
//			Pageable pageable
//	);



}
