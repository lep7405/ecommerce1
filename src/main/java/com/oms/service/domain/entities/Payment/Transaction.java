package com.oms.service.domain.entities.Payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.enums.PaymentMethodEnum;
import com.oms.service.domain.enums.StateTransaction;
import com.oms.service.domain.enums.TransactionType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "transaction")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name="pay_date")
	private Timestamp payDate;

	@Column(name = "deleted")
	private boolean deleted = false;

	@Column(name="payment_method")
	private String paymentMethodTransaction;

	@Column(name="state_transaction")
	@Enumerated(EnumType.STRING)
	private StateTransaction stateTransaction;

	@Column(name="transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Type(type = "jsonb")
	@Column(name = "transaction_data", columnDefinition = "jsonb")
	@JsonProperty("transactionData")
	private String transactionData;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	@JsonBackReference
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id")
	@JsonBackReference
	private PaymentMethod paymentMethod;
}
