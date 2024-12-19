package com.oms.service.domain.entities.Filter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.enums.FilterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "filters")
@Where(clause = "deleted = false")
public class Filters {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private FilterType filterType;

	@Column(name = "filterIndex")
	private Integer filterIndex;

	@Column(name = "deleted")
	private Boolean deleted;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@JsonBackReference
	private Category category;

	@ManyToOne
	@JoinColumn(name = "attribute_id")
	private Attribute attribute;

	@OneToMany(mappedBy = "filter",fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<FilterItem> listFilterItems;

	public void addFilterItem(FilterItem filterItem) {
		if(listFilterItems == null) {
			listFilterItems = new java.util.ArrayList<>();
		}
		listFilterItems.add(filterItem);
		filterItem.setFilter(this);
	}
}
