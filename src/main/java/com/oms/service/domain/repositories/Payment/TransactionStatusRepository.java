package com.oms.service.domain.repositories.Payment;

import com.oms.service.domain.entities.Payment.TranSactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface TransactionStatusRepository extends JpaRepository<TranSactionStatus,Long> {
	@Query("select t from TranSactionStatus t where t.status=:status and t.paymentMethod.id=:payment_method_id")
	Optional<TranSactionStatus> findByStatus(@Param("status") String status,@Param("payment_method_id") Long payment_method_id);
}
