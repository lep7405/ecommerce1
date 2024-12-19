package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.enums.ProgramType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

  List<ProductVariant> findByProductId(Long productId);

  @Query(
      "SELECT pv FROM ProductVariant pv "
          + "WHERE (:maxPrice IS NULL OR pv.price <= :maxPrice) "
          + "AND (:startDate IS NULL OR date(pv.createdAt) >=  cast(cast(:startDate as text) as date)) "
          + "AND (:endDate IS NULL OR  date(pv.createdAt) <= cast(cast(:endDate as text) as date)) "
          + "AND (:productId IS NULL OR pv.product.id = :productId)")
  Page<ProductVariant> findAllByPriceAndCreatedAt(
      @Param("maxPrice") BigDecimal maxPrice,
      @Param("startDate") String startDate,
      @Param("endDate") String endDate,
      @Param("productId") Long productId,
      Pageable pageable);

  @Query("SELECT pv FROM ProductVariant pv " +
          "JOIN pv.listRelDiscountProduct rdp " +
          "JOIN rdp.discount d " +
          "JOIN d.programDiscount pd " +
          "WHERE pv.id = :productVariantId " +
          "AND pd.programType = :programType " +
          "AND pd.startDate BETWEEN :startDate AND :endDate")
  Optional<ProductVariant> findConflictingProductVariants(
          @Param("productVariantId") Long productVariantId,
          @Param("programType") ProgramType programType,
          @Param("startDate") LocalDateTime startDate,
          @Param("endDate") LocalDateTime endDate);



//  @Query("SELECT r.productVariant FROM RelDiscountProduct r WHERE r.productVariant.id IN :listVariantId AND r.discount.programType = :programType")
//  List<ProductVariant> listVariantByDiscount(@Param("listVariantId") List<Long> listVariantId, @Param("programType") ProgramType programType);

}
