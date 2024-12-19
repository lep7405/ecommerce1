package com.oms.service.domain.entities.Refund;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oms.service.domain.entities.Order.OrderItem;
import com.oms.service.domain.enums.StateRefundExchange;
import com.oms.service.domain.enums.TypeRefundExchange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "refund_exchange_item")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RefundExchangeItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="fee_exchange")
	private BigDecimal fee_exchange;

	@Column(name="refund_amount")
	private BigDecimal refund_amount;

	@Column(name="quantity")
	private Integer quantity;

	@Type(type = "jsonb")
	@Column(name = "images", columnDefinition = "jsonb")
	private List<String> images;

	@Column(name="description_admin",columnDefinition = "TEXT")
	private String descriptionAdmin;

	@Column(name="description",columnDefinition = "TEXT")
	private String description;

	@Column(name="type_refund_exchange")
	@Enumerated(EnumType.STRING)
	private TypeRefundExchange typeRefundExchange;

	@Column(name="state_refund_exchange")
	@Enumerated(EnumType.STRING)
	private StateRefundExchange stateRefundExchange;

	@Column(name="deleted")
	private Boolean deleted;

	@Column(name="created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Column(name="updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private OrderItem orderItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_refund_exchange_id",nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private RequestRefundExchange requestRefundExchange;

}
