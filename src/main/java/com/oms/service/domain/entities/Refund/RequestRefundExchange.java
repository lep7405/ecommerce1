package com.oms.service.domain.entities.Refund;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.enums.StateRefundExchange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "request_refund_exchange")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RequestRefundExchange {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Column(name="updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@Column(name="deleted")
	private Boolean deleted;

	@Column(name="state_request_refund_exchange")
	@Enumerated(EnumType.STRING)
	private StateRefundExchange stateRequestRefundExchange;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private User user;

	@OneToMany(fetch = FetchType.LAZY,mappedBy = "requestRefundExchange",cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private List<RefundExchangeItem> listRefundExchangeItems;

	public void addRefundExchange(RefundExchangeItem refundExchangeItem){
		if(listRefundExchangeItems == null){
			listRefundExchangeItems = new ArrayList<>();
		}
		listRefundExchangeItems.add(refundExchangeItem);
		refundExchangeItem.setRequestRefundExchange(this);
	}

}
