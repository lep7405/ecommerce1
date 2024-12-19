package com.oms.service.domain.repositories.Filter;

import com.oms.service.domain.entities.Filter.FilterItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilterItemRepository extends JpaRepository<FilterItem,Long> {
}
