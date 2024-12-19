package com.oms.service.domain.entities.Filter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.oms.service.domain.entities.AttributeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "filter_item")
@Where(clause = "deleted = false")
public class FilterItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "mins")
	private Integer mins;

	@Column(name = "maxs")
	private Integer maxs;

	@Column(name = "name")
	private String name;

	@Column(name = "deleted")
	private Boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filter_id")
	@JsonBackReference
	private Filters filter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_value_id")
	@JsonBackReference
	private AttributeValue attributeValue;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp updatedAt;
}

