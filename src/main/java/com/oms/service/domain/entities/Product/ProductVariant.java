package com.oms.service.domain.entities.Product;

import com.fasterxml.jackson.annotation.*;
import com.oms.service.domain.entities.Cart.CartItem;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.RelDiscountProduct;
import com.oms.service.domain.entities.RelVariantValueProduct;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product_variant")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductVariant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "images",columnDefinition = "TEXT")
  private String images;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  @Column(name = "deleted")
  private Boolean deleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  @JsonBackReference
  private Product product;

  @OneToMany(
          mappedBy = "productVariant",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<RelVariantValueProduct> listRelVariantValueProduct;

  public void addRelVariantValueProduct(RelVariantValueProduct relVariantValueProduct) {
    if (listRelVariantValueProduct == null) {
      listRelVariantValueProduct = new ArrayList<>();
    }
    listRelVariantValueProduct.add(relVariantValueProduct);
    relVariantValueProduct.setProductVariant(this);
  }

  @OneToMany(
          mappedBy = "productVariant",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<CartItem> listCartItem;

  @OneToMany(
          mappedBy = "productVariant",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<RelDiscountProduct> listRelDiscountProduct;

  public void addRelDiscountProduct(RelDiscountProduct relDiscountProduct) {
    if (listRelDiscountProduct == null) {
      listRelDiscountProduct = new ArrayList<>();
    }
    listRelDiscountProduct.add(relDiscountProduct);
    relDiscountProduct.setProductVariant(this);
  }
}



//LocalDateTime timestampDateTime = timestamp.toLocalDateTime();
//// Lấy thời gian hiện tại
//LocalDateTime now = LocalDateTime.now();
//		if(timestampDateTime.isAfter(now)){
//        throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_DATE_RECEIVE_PRODUCT.val());
//        }
//
//int yearFromTimestamp = timestampDateTime.getYear();
//int monthFromTimestamp = timestampDateTime.getMonthValue();
//int dayFromTimestamp = timestampDateTime.getDayOfMonth();
//
//int currentYear = now.getYear();
//int currentMonth = now.getMonthValue();
//int currentDay = now.getDayOfMonth();
//
//// Tính số tháng
//int monthsDifference = (currentYear - yearFromTimestamp) * 12 + (currentMonth - monthFromTimestamp);
//		if (currentDay < dayFromTimestamp) {
//monthsDifference--;
//        }



