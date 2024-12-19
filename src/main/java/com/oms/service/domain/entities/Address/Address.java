package com.oms.service.domain.entities.Address;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.enums.TypeAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "address")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name="address_detail")
	private String addressDetail;

	@Column(name="phone_number")
	private String phoneNumber;

	@Column(name="full_name")
	private String fullName;

	@Column(name="type_address")
	@Enumerated(EnumType.STRING)
	private TypeAddress typeAddress;

	@Column(name="is_default")
	private Boolean isDefault;

	@Column(name = "deleted")
	private Boolean deleted ;

	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "province_code")
	@JsonBackReference
	private Province province;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "district_code")
	@JsonBackReference
	private District district;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ward_code")
	@JsonBackReference
	private Ward ward;

}
