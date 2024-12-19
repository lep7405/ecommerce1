package com.oms.service.domain.entities.Order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oms.service.domain.entities.Review;
import com.oms.service.domain.enums.StateOrderItem;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_item")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name="product_id")
	private Long productId;

	@Column(name = "product_variant_id")
	private Long productVariantId;

	@Column(name= "totalPrice")
	private BigDecimal totalPrice;

	@Column(name="quantity")
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(name="state_order_item")
	private StateOrderItem stateOrderItem;



	@Column(name="created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@Type(type = "jsonb")
	@Column(name = "productInfo", columnDefinition = "jsonb")
	@JsonProperty("productInfo")
	private ProductInfo productInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id",nullable = false)
	@JsonBackReference
	private Order order;

	@OneToOne(fetch = FetchType.LAZY,mappedBy = "orderitem",cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private Review review;



}
