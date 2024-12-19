package com.oms.service.domain.entities.Account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "admin")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin extends Account {
//	@OneToMany(
//			mappedBy = "admin",
//			fetch = FetchType.LAZY,
//			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
//	@JsonManagedReference
//	private List<Product> listProduct;
//
//	@OneToMany(
//			mappedBy = "admin",
//			fetch = FetchType.LAZY,
//			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
//	@JsonManagedReference
//	private List<ProductVariant> listProductVariant;
//
//	@OneToMany(
//			mappedBy = "admin",
//			fetch = FetchType.LAZY,
//			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
//	@JsonManagedReference
//	private List<ProductVariant> listProductVariant;
}
