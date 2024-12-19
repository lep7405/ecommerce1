package com.oms.service.domain.repositories.Order;

import com.oms.service.app.dtos.ProductSaleDto;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.enums.StateOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface OrderRepository extends JpaRepository<Order,Long> {
	@Query("SELECT o FROM Order o WHERE (:userId is null OR o.user.id = :userId) " +
			"AND (:state IS NULL OR o.stateOrder = :state) " +
			"AND (:isPayment IS NULL OR o.isPayment = :isPayment) " +
			"AND (:year IS NULL OR FUNCTION('YEAR', o.createdAt) = :year) " +
			"AND (:month IS NULL OR FUNCTION('MONTH', o.createdAt) = :month) " +
			"AND ((:startDate IS NULL OR :endDate IS NULL) OR (o.createdAt BETWEEN :startDate AND :endDate))")
	Page<Order> findAllOrder(
			@Param("userId") Long userId,
			@Param("state") StateOrder state,
			@Param(("isPayment")) Boolean isPayment,
			@Param("month") Integer month,
			@Param("year") Integer year,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,
			Pageable pageable);

	@Query("SELECT SUM(o.totalPrice),SUM(o.quantity) FROM OrderItem o WHERE o.productId = :id " +
			"AND o.stateOrderItem = :state " +
			"AND (:year IS NULL OR FUNCTION('YEAR', o.createdAt) = :year) " +
			"AND (:month IS NULL OR FUNCTION('MONTH', o.createdAt) = :month) " +
			"AND ((:startDate IS NULL OR :endDate IS NULL) OR (o.createdAt BETWEEN :startDate AND :endDate))")
	ProductSaleDto getSales(
			@Param("id") Long id,
			@Param("state") StateOrder state,
			@Param("month") Integer month,
			@Param("year") Integer year,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

}
