package com.oms.service.domain.entities.Address;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "wards")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ward {
	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name="name_en")
	private String nameEn;

	@Column(name="full_name")
	private String fullName;

	@Column(name="full_name_en")
	private String fullNameEn;

	@Column(name="code_name")
	private String codeName;

	@Column(name = "deleted")
	private Boolean deleted;

	@OneToMany(
			mappedBy = "ward",fetch =FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<Address> listAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "district_code")
	@JsonBackReference
	private District district;
}
