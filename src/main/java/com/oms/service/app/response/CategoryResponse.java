
package com.oms.service.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oms.service.domain.entities.Category;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse extends BaseResponse{

	private Long id;

	private String name;
	private String url;
	private Long parentId;

	private List<CategoryResponse> childrens;
	private List<ParameterResponse> listParameter;
	private List<AttributeResponse> listAttribute;

	private List<BrandResponse> listBrand;
	private List<TypeProductResponse> listTypeProduct;

	private Boolean isParameter;
}
