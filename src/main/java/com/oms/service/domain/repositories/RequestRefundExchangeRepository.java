package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Refund.RequestRefundExchange;
import com.oms.service.domain.enums.StateRefundExchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequestRefundExchangeRepository extends JpaRepository<RequestRefundExchange,Long> {
	@Query("select r from RequestRefundExchange r where r.user.id=:userId and r.stateRequestRefundExchange=:state ")
	Page<RequestRefundExchange> findAllByUserId(Long userId, StateRefundExchange state, Pageable pageable);

	@Query("select r from RequestRefundExchange r where r.stateRequestRefundExchange=:state ")
	Page<RequestRefundExchange> findAllByAdmin(StateRefundExchange state, Pageable pageable);
}
