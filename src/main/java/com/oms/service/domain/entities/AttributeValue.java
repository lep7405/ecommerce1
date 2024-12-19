package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Product.Attribute;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "attribute_value")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class AttributeValue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Type(type = "jsonb")
	@Column(name = "attValue", columnDefinition = "jsonb")
	private AttValue attValue;

	@Column(name = "deleted")
	private Boolean deleted;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_id", nullable = false)
	@JsonBackReference
	private Attribute attribute;

	@OneToMany(
			mappedBy = "attributeValue",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<RelVariantValueProduct> listRelVariantValueProduct;

	public void addRelVariantValueProduct(RelVariantValueProduct relVariantValueProduct) {
		if (listRelVariantValueProduct == null) {
			listRelVariantValueProduct = new ArrayList<>();
		}
		listRelVariantValueProduct.add(relVariantValueProduct);
		relVariantValueProduct.setAttributeValue(this);
	}
}
