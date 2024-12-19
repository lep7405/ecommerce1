package com.oms.service.domain.repositories.Payment;

import com.oms.service.domain.entities.Payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
