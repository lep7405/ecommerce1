package com.oms.service.domain.entities.Payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "payment_method")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentMethod {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name = "description")
	private String description;

	@Column(name="merchant_id")
	private String merchantId;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@OneToMany(mappedBy = "paymentMethod",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<TranSactionStatus> listTransactionStatus;

	@OneToMany(mappedBy = "paymentMethod",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Transaction> listTransaction;

	public void addTransaction(Transaction transaction) {
		if(listTransaction == null) {
			listTransaction = new ArrayList<>();
		}
		listTransaction.add(transaction);
		transaction.setPaymentMethod(this);
	}


}
