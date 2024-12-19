package com.oms.service.app.controllers;

import com.oms.service.app.dtos.Category.CategoryDto;
import com.oms.service.app.dtos.Category.CategoryDtoBase;
import com.oms.service.app.dtos.Category.CategoryDtoForCreate;
import com.oms.service.app.response.*;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.repositories.CategoryRepository;
import com.oms.service.domain.services.CategoryService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryRepository categoryRepository;
	@PostMapping
	public CategoryResponse createCategory(@RequestBody @Valid CategoryDtoBase categoryDtoBase) {
		return categoryService.createCategory(categoryDtoBase);
	}
	@PutMapping("/{id}")
	public CategoryResponse updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryDtoBase categoryDtoBase) {
		return categoryService.updateCategory(id, categoryDtoBase);
	}
	@PostMapping("/{id}/createParameter")
	public CategoryResponse createParameterToCategory(@PathVariable Long id,@RequestBody @Valid CategoryDtoForCreate categoryDto) {
		return categoryService.createParameterToCategory(id,categoryDto);
	}
	@PutMapping("/{id}/updateParameter")
	public CategoryResponse updateParameterToCategory(@PathVariable Long id,@RequestBody @Valid CategoryDto categoryDto) {
		return categoryService.updateParameterCategory(id,categoryDto);
	}

	//delete
	@DeleteMapping("/{id}")
	public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
		CategoryResponse categoryResponse= categoryService.deleteCategory(id);
		return new ApiResponse<>(HttpStatus.OK.value(),categoryResponse);
	}

	@DeleteMapping("/_bulk")
	public ApiResponse<?> deleteCategories(@RequestBody Long[] categoryIds) {
		categoryService.deleteCategories(categoryIds);
		return new ApiResponse<>(HttpStatus.OK.value(),categoryIds);
	}

	//lấy ra category cho việc cập nhật các paramter hoặc attribute
	@GetMapping("/{id}")
	public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
		CategoryResponse categoryResponse = categoryService.getCategoryById(id);
		return new ApiResponse<>(HttpStatus.OK.value(),categoryResponse);
	}
	//lấy ra tất cả category
	@GetMapping("/all")
	public ApiResponse<List<CategoryResponse>> getAll(){
		return new ApiResponse<>(HttpStatus.OK.value(),categoryService.getAll());
	}

	@GetMapping("/getCate/{id}")
	public Category getCate(@PathVariable Long id) {
		return categoryRepository.findById(id).get();
	}

	@GetMapping("/allForAdmin")
	public ApiResponse<List<CategoryResponse>> getAllForAdmin(){
		return new ApiResponse<>(HttpStatus.OK.value(),categoryService.getAllForAdmin());
	}

	@GetMapping("/getCategoryParamter/{id}")
	public ApiResponse<CategoryResponse> getCategoryParamter(@PathVariable Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),categoryService.getCategoryParameter(id));
	}
	@GetMapping("/getCategoryParameterForUpdateParameter/{id}")
	public ApiResponse<CategoryResponse> getCategoryParamter2(@PathVariable Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),categoryService.getCategoryParameterForUpdateParameter(id));
	}

}
