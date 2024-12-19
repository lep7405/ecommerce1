package com.oms.service.domain.entities.Product;

import com.fasterxml.jackson.annotation.*;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Cart.CartItem;
import com.oms.service.domain.enums.CommitmentEnum;
import com.oms.service.domain.enums.StateProduct;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "product")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;


  @Column(name = "description", columnDefinition = "TEXT")
  private String description;


  @Type(type = "jsonb")
  @Column(name = "images", columnDefinition = "jsonb")
  @JsonProperty("images")
  private List<Images> images;

  @Type(type = "jsonb")
  @Column(name = "averageReviews", columnDefinition = "jsonb")
  @JsonProperty("averageReviews")
  private AverageReview averageReview;

  @Column(name = "min_price")
  private BigDecimal minPrice;

  @Column(name = "max_price")
  private BigDecimal maxPrice;

  @Column(name = "max1Buy")
  private Integer max1Buy;

  @Type(type = "jsonb")
  @Column(name = "commitment", columnDefinition = "jsonb")
  @JsonProperty("commitment")
  private List<Commitment> listCommitment;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "state")
  private StateProduct state;

  @Column(name = "deleted")
  private Boolean deleted;

  @ManyToOne()
  @JoinColumn(name = "category_id")
  @JsonBackReference
  private Category category;

  @OneToMany(
          mappedBy = "product",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<ProductVariant> listProductVariants;

  @OneToMany(
          mappedBy = "product",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<RelVariantValueProduct> listRelVariantValueProduct;
  public void addRelVariantValueProduct(RelVariantValueProduct relVariantValueProduct) {
    if (listRelVariantValueProduct == null) {
      listRelVariantValueProduct = new ArrayList<>();
    }
    listRelVariantValueProduct.add(relVariantValueProduct);
    relVariantValueProduct.setProduct(this);
  }

  public void addProductVariant(ProductVariant productVariant) {
    if (listProductVariants == null) {
      listProductVariants = new ArrayList<>();
    }
    listProductVariants.add(productVariant);
    productVariant.setProduct(this);
  }

  @OneToMany(
          mappedBy = "product",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<CartItem> listCartItem;

  public void addCartItem(CartItem cartItem) {
    if (listCartItem == null) {
      listCartItem = new ArrayList<>();
    }
    listCartItem.add(cartItem);
    cartItem.setProduct(this);
  }

  @OneToMany(
          mappedBy = "product",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<RelDiscountProduct> listRelDiscountProduct;

  public void addRelDiscountProduct(RelDiscountProduct relDiscountProduct) {
    if (listRelDiscountProduct == null) {
      listRelDiscountProduct = new ArrayList<>();
    }
    listRelDiscountProduct.add(relDiscountProduct);
    relDiscountProduct.setProduct(this);
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "brand_id")
  @JsonBackReference
  private Brand brand;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "type_product_id")
  @JsonBackReference
  private TypeProduct typeProduct;

}
