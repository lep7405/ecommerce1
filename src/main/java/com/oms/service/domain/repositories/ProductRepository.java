package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.oms.service.domain.enums.StateProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByName(String name);
	Optional<Product> findById(Long id);
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.id in :ids")
	List<Product> findByIdAndLock(@Param("ids") List<Long> ids);


	@Query(
			value = "SELECT p.* FROM product p " +
					"JOIN rel_variant_value_product rel ON p.id = rel.product_id " +
					"JOIN attribute_value av ON av.id = rel.attribute_value_id " +
					"WHERE "+
					"(:name IS NULL OR LOWER(TRIM(p.name)) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
					"(:categoryId IS NULL OR p.category_id = :categoryId) AND " +
					"(:minPrice IS NULL OR p.min_price >= :minPrice) AND " +
					"(:maxPrice IS NULL OR p.min_price <= :maxPrice) AND " +
					"(av.id IN (:listAttributeId)) " +
					"GROUP BY p.id " +
					"HAVING COUNT(DISTINCT av.id) = :listAttributeSize", nativeQuery = true
	)
	Page<Product> filterProduct(
			@Param("name") String name,
			@Param("categoryId") Long categoryId,
			@Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice,
			@Param("listAttributeId") List<Long> listAttributeId,
			@Param("listAttributeSize") Integer listAttributeSize,
			Pageable pageable
	);

	@Transactional
	@Modifying
	@Query("UPDATE Product p SET p.deleted = true")
	void updateAllProductsToDeleted();



	@Query(value = """
    SELECT p.* 
    FROM product p
    JOIN rel_variant_value_product rel ON p.id = rel.product_id
    JOIN attribute_value av ON av.id = rel.attribute_value_id
    JOIN attribute a ON a.id = av.attribute_id
    JOIN filters f ON f.attribute_id = a.id
    JOIN filter_item ft ON ft.filter_id = f.id
    WHERE 
        (:minPrice IS NULL OR p.min_price >= :minPrice) AND
        (:maxPrice IS NULL OR p.max_price <= :maxPrice) AND
        (:name IS NULL OR LOWER(TRIM(p.name)) LIKE LOWER(CONCAT('%', :name, '%'))) AND
	   	(:brandId IS null or av.id=:brandId) AND
	   	(:state IS null or p.state=:state) AND
		(:typeProductId IS NULL OR av.id = :typeProductId) AND
		(:categoryId IS NULL OR p.category_id = :categoryId) AND
          (
          	CASE
          		when ft.attribute_value_id is not null then (:listFilterItemId is null or ft.attribute_value_id in (:listFilterItemId))
          		when (
                             CASE
                                 WHEN a.datatype = 'INTEGER' THEN ft.mins < CAST(av.attValue->>'attValueInt' AS INTEGER)
                                 WHEN a.datatype = 'DOUBLE' THEN ft.mins < CAST(av.attValue->>'attValueDouble' AS DOUBLE PRECISION)
                                 ELSE false
                             END
                         )
          	END
          )
    """, nativeQuery = true)
	Page<Product> filterProducts(
			@Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice,
			@Param("state") StateProduct stateProduct,
			@Param("name") String name,
			@Param("brandId") Long brandId,
			@Param("typeProductId") Long typeProductId,
			@Param("categoryId") Long categoryId,
			@Param("listFilterItemId") List<Long> listFilterItemId,
			Pageable pageable
	);



	@Query(value = "SELECT modify_json_field(:productId, :fieldName, :operation)", nativeQuery = true)
	Integer modifyReviewField(@Param("productId") Long productId, @Param("fieldName") String fieldName,@Param("operation") String operation);

	@Query("select p from Product p join fetch p.listProductVariants pv where p.id = :productId and pv.id in :variantId")
	Product findByIdAndVariant(@Param("productId") Long productId, @Param("variantId") List<Long> variantId);

	@Query("select p from Product p where p.category.id = :categoryId")
	List<Product> findAllAndCategoryId(Long categoryId);

	@Query(value = """
    WITH RECURSIVE CategoryTree AS (
        SELECT id
        FROM Category
        WHERE id = :categoryId
        UNION ALL
        SELECT c.id
        FROM Category c
        INNER JOIN CategoryTree ct ON c.parent_id = ct.id
    )
    SELECT p.*
    FROM Product p
    WHERE p.category_id IN (SELECT id FROM CategoryTree)
    """, nativeQuery = true)
	List<Product> findAllByCategoryAndSubcategories(@Param("categoryId") Long categoryId);

	@Query("select p from Product p " +
			"join p.listRelDiscountProduct rvp " +
			"join rvp.discount d " +
			"join d.programDiscount pd " +
			"where p.id in :ids " +
			"and rvp.isMainProduct = true " +
			"and pd.programType = 'DISCOUNT_COMBO_GIFT_PRODUCT' " +
			"and ((pd.endDate >= :startDate and pd.endDate <= :endDate) " +
			"or (pd.startDate >= :startDate and pd.startDate <= :endDate))")
	List<Product> findA(@Param("ids") List<Long> ids,
						@Param("startDate") LocalDateTime startDate,
						@Param("endDate") LocalDateTime endDate);

	@Query("select p from Product p " +
			"join p.listRelDiscountProduct rvp " +
			"join rvp.discount d " +
			"join d.programDiscount pd " +
			"where p.id in :ids " +
			"and rvp.isMainProduct = true " +
			"and pd.programType = 'DISCOUNT_COMBO' " +
			"and ((pd.endDate >= :startDate and pd.endDate <= :endDate) " +
			"or (pd.startDate >= :startDate and pd.startDate <= :endDate))")
	List<Product> findB(@Param("ids") List<Long> ids,
						@Param("startDate") LocalDateTime startDate,
						@Param("endDate") LocalDateTime endDate);
	@Query("select p from Product p " +
			"join p.listRelDiscountProduct rvp " +
			"join rvp.discount d " +
			"where d.id=:discountId ")
	List<Product> findAllByDiscount(@Param("discountId") Long discountId);


	List<Product> findAllByIdNotIn(List<Long> ids);
}
