package com.oms.service.domain.services;

import com.oms.service.app.dtos.Category.CategoryDto;
import com.oms.service.app.dtos.Category.CategoryDtoBase;
import com.oms.service.app.dtos.Category.CategoryDtoForCreate;
import com.oms.service.app.response.CategoryResponse;
import com.oms.service.domain.entities.Category;

import java.util.List;

public interface CategoryService {

	CategoryResponse createCategory(CategoryDtoBase categoryDtoBase);

	CategoryResponse updateCategory(Long id, CategoryDtoBase categoryDtoBase);
	CategoryResponse createParameterToCategory(Long id, CategoryDtoForCreate categoryDto);

	CategoryResponse updateParameterCategory(Long id, CategoryDto categoryDto);

	CategoryResponse getCategoryById(Long id);
	List<CategoryResponse> getAll();

	CategoryResponse deleteCategory(Long id);

	void deleteCategories(Long[] ids);

	List<CategoryResponse> getAllForAdmin();
	CategoryResponse getCategoryParameter(Long id);
	CategoryResponse getCategoryParameterForUpdateParameter(Long id);
//	Category getCategoryForCreateProduct(Long id);

//	Category findNearestCategoryWithListParameter(Category category) throws ExceptionOm;

//	AttributeFilterResponse getFilterAttribute(Long id);


}
