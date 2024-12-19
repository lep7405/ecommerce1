package com.oms.service.domain.entities.Discount;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.RelDiscountProduct;
import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "program_discount")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProgramDiscount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="code")
	private String code;

	@Column(name="program_discount_type")
	@Enumerated(EnumType.STRING)
	private ProgramDiscountType programDiscountType;

	@Column(name="program_type")
	@Enumerated(EnumType.STRING)
	private ProgramType programType;

	@Column(name="deleted")
	private boolean deleted;

	@CreationTimestamp
	@Column(name = "endDate_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endDate;

	@CreationTimestamp
	@Column(name = "startDate_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startDate;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@Column(name = "updated_at",updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@OneToMany(
			mappedBy = "programDiscount",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Discount> listDiscount;


	public void addDiscount(Discount discount) {
		if(listDiscount == null) {
			listDiscount = new ArrayList<>();
		}
		listDiscount.add(discount);
		discount.setProgramDiscount(this);
	}
}
