package com.oms.service.domain.entities.Account;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Account.Account;
import com.oms.service.domain.entities.Address.Address;
import com.oms.service.domain.entities.Cart.Cart;
import com.oms.service.domain.entities.Order.Order;
import com.oms.service.domain.entities.Review;
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
@Table(name = "users")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends Account {

	@OneToOne (mappedBy = "user",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private Cart cart;

	@OneToMany (mappedBy = "user",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Order> listOrders;

	public void addOrder(Order order){
		if(listOrders == null){
			listOrders = new ArrayList<>();
		}
		listOrders.add(order);
		order.setUser(this);
	}

	@OneToMany(
			mappedBy = "user",fetch =FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Review> listReview;

	public void addReview(Review review){
		if(listReview == null){
			listReview = new ArrayList<>();
		}
		listReview.add(review);
		review.setUser(this);
	}

	@OneToMany(
			mappedBy = "user",fetch =FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Address> listAddress;

	public void addAddress(Address address){
		if(listAddress == null){
			listAddress = new ArrayList<>();
		}
		listAddress.add(address);
		address.setUser(this);
	}
}
