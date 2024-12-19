package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Product.TypeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeProductRepository extends JpaRepository<TypeProduct,Long> {
	@Query("select t from TypeProduct t where t.category.id=:catogoryId and t.id in :listTypeProductId")
	List<TypeProduct> findAllByIdAndCategoryId(Long catogoryId, List<Long> listTypeProductId);
}
