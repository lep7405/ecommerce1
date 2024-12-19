package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.app.response.Review.ReviewResponse;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Product.Images;
import com.oms.service.domain.enums.StateProduct;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
	private Long id;
	private String name;
	private String url;
	private BrandResponse brand;



	private TypeProductResponse typeProduct;
	private String description;
	private List<Images> images;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Integer max1Buy;
	private StateProduct state;



	private List<ParameterResponse> listRelVariantValueProduct;
	private List<ProductVariantResponse> listProductVariants;

	private List<ReviewResponse> listReview;
	private AverageReview averageReview;
	private Long categoryId;
	private String categoryName;
}


