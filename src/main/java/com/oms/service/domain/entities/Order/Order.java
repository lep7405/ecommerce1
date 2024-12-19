package com.oms.service.domain.entities.Order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Payment.Transaction;
import com.oms.service.domain.entities.UserInfo;
import com.oms.service.domain.enums.StateOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Where(clause = "deleted = false")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "state_order")
	private StateOrder stateOrder;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@Column(name="is_payment")
	private Boolean isPayment;

	@Type(type = "jsonb")
	@Column(name = "userInfo", columnDefinition = "jsonb")
	@JsonProperty("userInfo")
	private UserInfo userInfo;

	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name = "payment_method_id")
	private Long paymentMethodId;

	@UpdateTimestamp
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@OneToMany(
			mappedBy = "order",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<OrderItem> listOrderItems;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	@JsonBackReference
	private User user;

	public void addOrderItem(OrderItem orderItem) {
		if(listOrderItems == null) {
			listOrderItems = new java.util.ArrayList<>();
		}
		listOrderItems.add(orderItem);
		orderItem.setOrder(this);
	}

	@OneToMany(
			mappedBy = "order",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<LogOrder> listOrderStateHistory;

	public void addOrderStateHistory(LogOrder orderStateHistory) {
		if(listOrderStateHistory == null) {
			listOrderStateHistory = new java.util.ArrayList<>();
		}
		listOrderStateHistory.add(orderStateHistory);
		orderStateHistory.setOrder(this);
	}

	@OneToMany(
			mappedBy = "order",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Transaction> listTransaction;

	public void addTransaction(Transaction transaction) {
		if(listTransaction == null) {
			listTransaction = new java.util.ArrayList<>();
		}
		listTransaction.add(transaction);
		transaction.setOrder(this);
	}
}

