package com.oms.service.domain.repositories.Order;

import com.oms.service.domain.entities.Order.LogOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogOrderRepository extends JpaRepository<LogOrder,Long> {
}
