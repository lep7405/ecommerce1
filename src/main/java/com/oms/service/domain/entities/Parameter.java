package com.oms.service.domain.entities;

import com.fasterxml.jackson.annotation.*;
import com.oms.service.domain.entities.Product.Attribute;
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
@Table(name = "parameter")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "group_index")
    private Integer groupIndex;

    @Column(name = "deleted")
    private Boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

//    @ManyToMany(
//            fetch = FetchType.LAZY,
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
//    @JoinTable(
//            name = "rel_attribute_value_product",
//            joinColumns = @JoinColumn(name = "product_id"),
//            inverseJoinColumns = @JoinColumn(name = "attribute_value_id"))
//    @JsonManagedReference
//    private List<AttributeValue> listAttributeValues;

    @OneToMany(mappedBy = "parameter",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JsonManagedReference
    private List<Attribute> listAttributes;
    public void addAttribute(Attribute attribute) {
        if (listAttributes == null) {
            listAttributes = new ArrayList<>();
        }
        listAttributes.add(attribute);
        attribute.setParameter(this);
    }
    public void removeAttribute(Attribute attribute) {
        if (listAttributes == null) {
            listAttributes = new ArrayList<>();
        }
        listAttributes.remove(attribute);
    }

   @ManyToOne()
   @JoinColumn(name = "category_id")
   @JsonBackReference
   private Category category;

}
