package com.oms.service.domain.repositories.Filter;

import com.oms.service.domain.entities.Filter.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpInc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilterRepository extends JpaRepository<Filters,Long> {
	Optional<Filters>findByNameIgnoreCase(String name);


	List<Filters> findAllByNameInIgnoreCase(Collection<String> name);
}
