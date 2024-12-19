package com.oms.service.domain.entities.Product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.Parameter;
import com.oms.service.domain.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "attribute")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Attribute {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "data_type")
	@Enumerated(EnumType.STRING)
	private DataType dataType;

	//attribute cần có , và attribute có thể có
	@Column(name = "is_Required")
	private Boolean isRequired;

	//attribute ảnh hưởng đến thông số của product như là ram, storage
	@Column(name = "is_For_Variant")
	private Boolean isForVariant;

	//Những attribute nào cho phép chọn giá trị, còn có những attribute phải điền tay
	@Column(name = "is_Select")
	private Boolean isSelect;

	@Column(name = "is_Select_Multiple")
	private Boolean isSelectMultiple;
	@Column(name = "deleted")
	private Boolean deleted;

	@Column(name = "created_at", updatable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "attribute", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonManagedReference
	private List<AttributeValue> listAttributeValue;

	public void addAttributeValue(AttributeValue attributeValue) {
		if (listAttributeValue == null) {
			listAttributeValue = new ArrayList<>();
		}
		listAttributeValue.add(attributeValue);
		attributeValue.setAttribute(this);
	}

	public void removeAttributeValue(AttributeValue attributeValue) {
		listAttributeValue.remove(attributeValue);
		attributeValue.setAttribute(null);
	}

	@ManyToOne
	@JoinColumn(name = "parameter_id")
	@JsonBackReference
	private Parameter parameter;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoty_id")
	@JsonBackReference
	private Category category;

}
