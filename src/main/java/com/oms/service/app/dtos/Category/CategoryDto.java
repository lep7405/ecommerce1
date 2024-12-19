package com.oms.service.app.dtos.Category;

import com.oms.service.app.dtos.TypeProductDto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
public class CategoryDto {

	@Valid
	@NotEmpty(message = "parameterDtoList must not be null.")
	private List<ParameterDto> parameterDtoList;
	private List<AttributeDto> listAttributes;

	@NotEmpty(message = "listValueBrandIds must not be null.")
	private List<Long> listValueBrandIds;

//	@NotNull(message = "typeProductList must not be null.")
	@NotEmpty(message = "listTypeProductDto must not be null.")
	private List<TypeProductDto> listTypeProductDto;
}


//package com.oms.service.app.dtos.CategoryS;
//
//import com.oms.service.app.dtos.AttributeDto;
//import com.oms.service.app.dtos.ParameterDto;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.util.List;
//import java.util.Set;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class CategoryDto  {
//	@NotEmpty(message = "listValueBrandIds must not be null.")
//	private List<Long> listValueBrandIds;
//	@NotEmpty(message = "parameterDtoList must not be null.")
//	private List<ParameterDto> parameterDtoList;
//
//	private List<AttributeDto> listAttributes;
//	@NotEmpty(message = "typeProductList must not be null.")
//	private List<String> typeProductList;
//}
