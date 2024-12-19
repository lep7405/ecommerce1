package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Discount.Discount;
import com.oms.service.domain.entities.Filter.Filters;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.TypeProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "category")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name="image_url",columnDefinition = "TEXT")
  private String url;

  @Column(name = "lever")
  private Integer lever;

  @Column(name = "deleted")
  private Boolean deleted;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  @OneToMany(
      mappedBy = "category",
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<Product> listProducts;
  public void addProduct(Product product) {
    if (listProducts == null) {
      listProducts = new ArrayList<>();
    }
    listProducts.add(product);
    product.setCategory(this);
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_id")
  @JsonBackReference
  private Category parentCategory;

  @OneToMany(
      mappedBy = "parentCategory",
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<Category> childrens;

  public int calculateLevel() {
    int level = 1;
    Category currentParent = this.parentCategory;
    while (currentParent != null) {
      level++;
      currentParent = currentParent.getParentCategory();
    }
    this.lever = level;
    return this.lever;
  }


  @OneToMany(
          mappedBy = "category",
          fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  @Fetch(FetchMode.SUBSELECT)
  private List<Parameter> listParameter;

  public void addParameter(Parameter parameter) {
    if (listParameter == null) {
      listParameter = new ArrayList<>();
    }
    listParameter.add(parameter);
    parameter.setCategory(this);
  }


  @OneToMany(
          mappedBy = "category",
          fetch = FetchType.EAGER,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  @Fetch(FetchMode.SUBSELECT)
  private List<Attribute> listAttribute;

  public void addAttribute(Attribute attribute) {
    if (listAttribute == null) {
      listAttribute = new ArrayList<>();
    }
    listAttribute.add(attribute);
    attribute.setCategory(this);
  }

  @OneToMany(
          mappedBy = "category",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<Filters> listFilter;

  @ManyToMany(
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JoinTable(
          name = "rel_brand_category",
          joinColumns = @JoinColumn(name = "category_id"),
          inverseJoinColumns = @JoinColumn(name = "brand_id"))
  @Fetch(FetchMode.SELECT)
  private List<Brand> listBrand;

  public void addBrand(Brand brand) {
    if (listBrand == null) {
      listBrand = new ArrayList<>();
    }
    listBrand.add(brand);
  }



  @OneToMany(
          mappedBy = "category",
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JsonManagedReference
  private List<TypeProduct> listTypeProduct;

  public void addTypeProduct(TypeProduct typeProduct) {
    if (listTypeProduct == null) {
      listTypeProduct = new ArrayList<>();
    }
    listTypeProduct.add(typeProduct);
    typeProduct.setCategory(this);
  }


  @ManyToMany(
          fetch = FetchType.LAZY,
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @JoinTable(
          name = "rel_discount_category",
          joinColumns = @JoinColumn(name = "category_id"),
          inverseJoinColumns = @JoinColumn(name = "discount_id"))
  @JsonManagedReference
  private List<Discount> listDiscount;

}



