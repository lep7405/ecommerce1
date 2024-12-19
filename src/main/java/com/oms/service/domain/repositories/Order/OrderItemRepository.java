package com.oms.service.domain.repositories.Order;

import com.oms.service.domain.entities.Order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
}
