package com.oms.service.domain.repositories.Discount;

import com.oms.service.domain.entities.Discount.Discount;
import com.oms.service.domain.entities.Discount.ProgramDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ProgramDiscountRepository extends JpaRepository<ProgramDiscount,Long> {
	@Query("SELECT pd FROM ProgramDiscount pd " +
			"WHERE (:programType IS NULL OR pd.programDiscountType = :programType) " +
			"AND (LOWER(pd.name) LIKE LOWER(CONCAT('%', COALESCE(CAST(:name AS text), ''), '%'))) " +
			"AND (COALESCE(:startDate, pd.startDate) = pd.startDate OR pd.startDate >= :startDate) " +
			"AND (COALESCE(:endDate, pd.endDate) = pd.endDate OR pd.endDate <= :endDate)")
	Page<ProgramDiscount> findAll(
			@Param("programType") String programType,
			@Param("name") String name,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			Pageable pageable
	);

	ProgramDiscount findByName(String name);
}
