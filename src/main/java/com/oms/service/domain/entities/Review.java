package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.*;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Order.OrderItem;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "reviews")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "deleted")
	private Boolean deleted;
	private Integer rateNumber;
	private String reviewBody;

	@Column(name = "created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	@JsonBackReference
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id")
	@JsonBackReference
	private OrderItem orderitem;

	@Type(type = "jsonb")
	@Column(name = "list_url", columnDefinition = "jsonb")
	@JsonProperty("listUrl")
	private List<String> listUrl = new ArrayList<>();


//	private List<String> images;


}
