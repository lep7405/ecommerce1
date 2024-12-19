package com.oms.service.domain.entities.Discount;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.RelDiscountProduct;
import com.oms.service.domain.enums.CustomerGroup;
import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramType;
import com.oms.service.domain.enums.PromotionType;
import io.micrometer.core.annotation.Counted;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "discount")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Discount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;



	@Column(name="purchase_limit")
	private Integer purchaseLimit;

	@Column(name="quantity_limit")
	private Integer quantityLimit;

	@Column(name="deleted")
	private boolean deleted;

	@Column(name="discount_type")
	@Enumerated(EnumType.STRING)
	private DiscountType discountType;

	@Column(name="discount_amount")
	private BigDecimal discountAmount;

	@Column(name="discount_percentage")
	private BigDecimal discountPercentage;

	@Column(name="max_discount_amount")
	private BigDecimal maxDiscountAmount;

	@Column(name = "min_order_amount")
	private BigDecimal minOrderAmount;

	@OneToMany(
			mappedBy = "discount",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<RelDiscountProduct> listRelDiscountProduct;

	public void addRelDiscountProduct(RelDiscountProduct relDiscountProduct) {
		if(listRelDiscountProduct == null) {
			listRelDiscountProduct =new ArrayList<>();
		}
		listRelDiscountProduct.add(relDiscountProduct);
		relDiscountProduct.setDiscount(this);
	}

	@ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "rel_discount_category",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonBackReference
    private List<Category> listCategory;

	@ManyToOne
	@JoinColumn(name = "program_discount_id")
	@JsonBackReference
	private ProgramDiscount programDiscount;




}
