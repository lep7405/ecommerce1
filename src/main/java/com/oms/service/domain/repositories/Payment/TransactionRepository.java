package com.oms.service.domain.repositories.Payment;

import com.oms.service.domain.entities.Payment.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
