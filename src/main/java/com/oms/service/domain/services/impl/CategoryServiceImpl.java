package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Category.*;
import com.oms.service.app.dtos.TypeProductDto;
import com.oms.service.app.response.*;
import com.oms.service.domain.Utils.AttributeUtils;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.Product.TypeProduct;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.*;
import com.oms.service.domain.services.CategoryService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final AttributeValueRepository attributeValueRepository;
	private final AttributeRepository attributeRepository;
	private final TypeProductRepository typeProductRepository;
	private final BrandRepository brandRepository;
	private final ModelMapper mapper;

	@Override
	@Transactional
	public CategoryResponse createCategory(CategoryDtoBase categoryDtoBase) {

		Optional<Category> categoryCheckName = categoryRepository.findByNameIgnoreCase(categoryDtoBase.getName());
		if (categoryCheckName.isPresent()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_NAME_EXISTS);
		}
		Category parentCategory = null;
		if (categoryDtoBase.getParentId() != null) {
			parentCategory = categoryRepository.findById(categoryDtoBase.getParentId())
					.orElseThrow(() ->
							new ExceptionOm(
									HttpStatus.BAD_REQUEST, ErrorMessageOm.PARENT_CATEGORY_NOT_FOUND.val()));
		}

		Category newCategory = new Category();

		newCategory.setName(categoryDtoBase.getName());
		if (parentCategory != null) {
			newCategory.setParentCategory(parentCategory);
			newCategory.setLever(parentCategory.getLever() + 1);
		} else {
			newCategory.setLever(1);
		}
		newCategory.setDeleted(false);
		newCategory.setCreatedAt(LocalDateTime.now());
		newCategory.setUrl(categoryDtoBase.getUrl());
		categoryRepository.save(newCategory);
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setListAttribute);
			mapper.skip(CategoryResponse::setListParameter);
			mapper.map(source->source.getParentCategory().getId(),CategoryResponse::setParentId);
		});
		return mapper.map(newCategory, CategoryResponse.class);
	}
	@Override
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryDtoBase categoryDtoBase) {
		Category categoryUpdate = categoryRepository.findById(id)
				.orElseThrow(
						() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.CATEGORY_NOT_FOUND));
		if(!Objects.equals(categoryDtoBase.getName(), categoryUpdate.getName())){
			Optional<Category> categoryCheckName = categoryRepository.findByNameIgnoreCase(categoryDtoBase.getName());
			if (categoryCheckName.isPresent()) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_NAME_EXISTS);
			}
			categoryUpdate.setName(categoryDtoBase.getName());
		}

//		if (categoryDtoBase.getParentId() != null) {
//			Category parentCategory = categoryRepository.findById(categoryDtoBase.getParentId())
//					.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PARENT_CATEGORY_NOT_FOUND.val()));
//			categoryUpdate.setParentCategory(parentCategory);
//			categoryUpdate.setLever(parentCategory.getLever() + 1);
//
//		} else {
//			categoryUpdate.setParentCategory(null);
//		}

		categoryUpdate.setUpdatedAt(LocalDateTime.now());
		categoryUpdate.setDeleted(false);
		categoryUpdate.setUrl(categoryDtoBase.getUrl());

		categoryRepository.save(categoryUpdate);
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setListAttribute);
			mapper.skip(CategoryResponse::setListParameter);
		});

		return mapper.map(categoryUpdate, CategoryResponse.class);
	}
	@Override
	@Transactional
	public CategoryResponse createParameterToCategory(Long id, CategoryDtoForCreate categoryDto) {
		//valid input
		Category category = categoryRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.CATEGORY_NOT_FOUND));

		List<Brand> listBrand=brandRepository.findAllById(categoryDto.getListValueBrandIds());
		if(listBrand.size()!=categoryDto.getListValueBrandIds().size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_BRAND);
		}

		validateParameters(categoryDto.getParameterDtoList(),false);
		List<AttributeDto>	attributeDtoList=null;
		if(categoryDto.getListAttributes()!=null){
			attributeDtoList=categoryDto.getListAttributes();
			validateAttributes(categoryDto.getListAttributes(),false);
		}
		List<String> typeProductList = categoryDto.getTypeProductList();

//		List<String> typeProductList = categoryDto.getListTypeProductDto().stream().map(TypeProductDto::getName).toList();
		Set<String> uniqueTypes = new HashSet<>(typeProductList);

		if (uniqueTypes.size() != typeProductList.size()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_TYPEPRODUCT);
		}
		categoryDto.getParameterDtoList().forEach(parameterDto -> {
			Parameter parameter = createParameter(parameterDto);
			category.addParameter(parameter);
		});

		if(attributeDtoList!=null){
			for(AttributeDto attributeDto:attributeDtoList){
				Attribute attribute=createAttribute(attributeDto);
				category.addAttribute(attribute);
			}
		}

		for(Brand brand:listBrand){
			category.addBrand(brand);
		}

		for(String typeProduct:typeProductList){
			TypeProduct typeProductNew = new TypeProduct();
			typeProductNew.setName(typeProduct);
			typeProductNew.setDeleted(false);
			typeProductNew.setCreatedAt(LocalDateTime.now());
			typeProductRepository.save(typeProductNew);
			category.addTypeProduct(typeProductNew);
		}
		categoryRepository.save(category);

		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setChildrens);
		});

		return mapper.map(category, CategoryResponse.class);
	}
	@Override
	@Transactional
	public CategoryResponse updateParameterCategory(Long id, CategoryDto categoryDto) {
		//validate
		List<AttributeValue> listAttributeValueBrand=attributeValueRepository.findAllById(categoryDto.getListValueBrandIds());
		if(listAttributeValueBrand.size()!=categoryDto.getListValueBrandIds().size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_BRAND);
		}

		Category categoryUpdate = categoryRepository.findById(id)
				.orElseThrow(() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.CATEGORY_NOT_FOUND));
		validateParameters(categoryDto.getParameterDtoList(),true);

		if(categoryDto.getListAttributes()!=null){
			validateAttributes(categoryDto.getListAttributes(),true);
		}
		List<Long> listTypeProductId=categoryDto.getListTypeProductDto().stream().map(TypeProductDto::getId).toList();
		List<TypeProduct> listTypeProduct=typeProductRepository.findAllByIdAndCategoryId(id,listTypeProductId);
		if(listTypeProduct.size()!=listTypeProductId.size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_TYPEPRODUCT);
		}

		//cập nhật parameter
		Map<Long, Parameter> mapParameter = categoryUpdate.getListParameter()
				.stream().collect(Collectors.toMap(Parameter::getId, para -> para));
		updateParametersAndAttributes(categoryDto, categoryUpdate,mapParameter);
		//cập nhật attribute
		if(categoryDto.getListAttributes()!=null){
			updateParameters(null,null,categoryUpdate,categoryDto);

		}
		//cập nhật brand
		List<Long> listIdBrandDto= categoryDto.getListValueBrandIds();
		List<Long> listIdBrandOld= categoryUpdate.getListBrand().stream().map(Brand::getId).toList();
		List<Long> listIdBrandDelete= listIdBrandOld.stream().filter(i -> !listIdBrandDto.contains(i)).toList();
		List<Long> listIdBrandAdd= listIdBrandDto.stream().filter(i -> !listIdBrandOld.contains(i)).toList();

		DeleteListBrandFromCategory(listIdBrandDelete,categoryUpdate);
		AddListBrandToCategory(listIdBrandAdd,categoryUpdate);

		List<Long> listIdTypeProductOld= categoryUpdate.getListTypeProduct().stream().map(TypeProduct::getId).toList();

		List<Long> listIdTypeProductDelete= listIdTypeProductOld.stream().filter(i -> !listTypeProductId.contains(i)).toList();
		List<TypeProduct> listTypeProductForDelete= categoryUpdate.getListTypeProduct().stream().filter(typeProduct -> listIdTypeProductDelete.contains(typeProduct.getId())).toList();
		List<TypeProductDto> listTypeProductAdd= categoryDto.getListTypeProductDto().stream().filter(typeProductDto -> typeProductDto.getId()==null).toList();

		List<Long> listTypeProductIdForUpdate= categoryDto.getListTypeProductDto().stream().map(TypeProductDto::getId).filter(typeProductDtoId -> typeProductDtoId !=null).toList();
		List<TypeProduct> listTypeProductForUpdate= categoryUpdate.getListTypeProduct().stream().filter(typeProduct -> listTypeProductIdForUpdate.contains(typeProduct.getId())).toList();


		DeleteListTypeProductFromCategory(listTypeProductForDelete);
		AddListTypeProductToCategory(listTypeProductAdd,categoryUpdate);
		UpdateListTypeProductToCategory(listTypeProductAdd,listTypeProductForUpdate);


		categoryUpdate.setUpdatedAt(LocalDateTime.now());
		categoryUpdate.setDeleted(false);
		categoryRepository.save(categoryUpdate);
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setChildrens);
		});
		return mapper.map(categoryUpdate, CategoryResponse.class);
	}
	//validate
	private void validateParameters(List<ParameterDto> parameterDtoList,Boolean isForUpdate) {

		Set<String> uniqueNames = new HashSet<>();
		Set<Integer> uniqueGroupIndices = new HashSet<>();
		for (ParameterDto parameterDto : parameterDtoList) {
			String lowerCaseName = parameterDto.getName().toLowerCase();
			log.info("hello"+parameterDto.getName());

			int groupIndex = parameterDto.getGroupIndex();

			if (!uniqueNames.add(lowerCaseName)) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_PARAMETER_NAME);
			}

			if (!uniqueGroupIndices.add(groupIndex)) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_GROUP_INDEX);
			}

			validateAttributes(parameterDto.getListAttributeDto(),isForUpdate);
		}
	}
	private void validateAttributes(List<AttributeDto> attributeDtoList,Boolean isForUpdate) {
		Set<String> uniqueAttributeNames = new HashSet<>();
		for (AttributeDto attributeBaseDto : attributeDtoList) {
			log.info("hello2"+attributeBaseDto.getName());

			String attributeName = attributeBaseDto.getName().toLowerCase();
			// Kiểm tra trùng tên Attribute trong mỗi Parameter
			if (!uniqueAttributeNames.add(attributeName)) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_NAME_ATTRIBUTE);
			}
			//
			boolean isSelect = attributeBaseDto.getIsSelect();
			int attributeValueSize =isForUpdate?attributeBaseDto.getListAttributeValueDto().size(): attributeBaseDto.getListAttributeValues().size();

			if ((isSelect && attributeValueSize == 0) || (!isSelect && attributeValueSize > 0) ||
					(attributeBaseDto.getIsSelectMultiple() && !isSelect)
			) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_VALUE+attributeBaseDto.getName());
			}
            if(attributeBaseDto.getIsSelect()&&attributeBaseDto.getIsRequired()){
				validateAttributeValues(attributeBaseDto,isForUpdate);
			}
		}
	}

	private void validateAttributeValues(AttributeDto attributeBaseDto,Boolean isForUpdate) {
		if(!isForUpdate){
			if(attributeBaseDto.getListAttributeValues()==null||attributeBaseDto.getListAttributeValues().isEmpty()){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_LIST_ATTRIBUTE_VALUES +attributeBaseDto.getName());
			}
		}
		List<?> attributeValues =isForUpdate?attributeBaseDto.getListAttributeValueDto().stream().map(AttributeValueDto::getAttValue).toList(): attributeBaseDto.getListAttributeValues();
		// Kiểm tra giá trị không trùng lặp
		Set<Object> uniqueValues = new HashSet<>(attributeValues);
		if (uniqueValues.size() < attributeValues.size()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_ATTRIBUTE_VALUE);
		}
		// Kiểm tra kiểu dữ liệu của các phần tử
		switch (attributeBaseDto.getDataType()) {
			case STRING:
				if (!attributeValues.stream().allMatch(value -> value instanceof String)) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_DATA_TYPE);
				}
				break;
			case INTEGER:
				if (!attributeValues.stream().allMatch(value -> value instanceof Integer)) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_DATA_TYPE);
				}
				break;
			case DOUBLE:
				if (!attributeValues.stream().allMatch(value -> value instanceof Double)) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_DATA_TYPE);
				}
				break;
			default:
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_DATA_TYPE);
		}
	}

	//Tạo
	private Parameter createParameter(ParameterDto parameterDto) {
		Parameter parameter = new Parameter();
		parameter.setName(parameterDto.getName());
		parameter.setDeleted(false);
		parameter.setCreatedAt(LocalDateTime.now());
		parameter.setGroupIndex(parameterDto.getGroupIndex());

		parameterDto.getListAttributeDto().forEach(attributeDto -> {
			Attribute attribute = createAttribute(attributeDto);
			parameter.addAttribute(attribute);
		});

		return parameter;
	}
	private Attribute createAttribute(AttributeDto attributeDto) {
		return AttributeUtils.createAttribute(attributeDto,true);
	}

	//Cập nhật brand
	public void AddListBrandToCategory(List<Long> listValueBrandIds,Category category) {
		for(Long brandId:listValueBrandIds) {
			Brand brand=category.getListBrand().stream().filter(brand1 -> brand1.getId().equals(brandId)).findFirst().orElse(null);
			category.getListBrand().add(brand);
		}
	}
	public void DeleteListBrandFromCategory(List<Long> listValueBrandIds,Category category) {
		for(Long brandId:listValueBrandIds) {
			Brand brand=category.getListBrand().stream().filter(brand1 -> brand1.getId().equals(brandId)).findFirst().orElse(null);
			category.getListBrand().remove(brand);
		}
	}


	//Cập nhật paramter trong category
	public void updateParametersAndAttributes(CategoryDto categoryDto, Category categoryUpdate,Map<Long, Parameter> mapParameter) {
		List<ParameterDto> listParameterDto = categoryDto.getParameterDtoList();
		List<ParameterDto> listParameterNew = listParameterDto.stream().filter(para -> para.getId() == null).collect(Collectors.toList());
		List<ParameterDto> listParameterUpdate = listParameterDto.stream().filter(para -> para.getId() != null).toList();

		List<Long> listIdParameterUpdate = listParameterUpdate.stream().map(ParameterDto::getId).toList();
		List<Long> listIdParameterOld = categoryUpdate.getListParameter().stream().map(Parameter::getId).toList();

		// Danh sách các ID parameter cần xóa
		List<Long> listIdParameterDelete = listIdParameterOld.stream().filter(id -> !listIdParameterUpdate.contains(id)).collect(Collectors.toList());
		// Xóa các parameter không còn trong danh sách
		deleteParameters(categoryUpdate.getListParameter(), listIdParameterDelete);
		// Thêm mới các parameter
		addNewParameters(categoryUpdate, listParameterNew);

		// Cập nhật các parameter hiện có
		for (ParameterDto parameterDto : listParameterUpdate) {
			Parameter parameterDb = mapParameter.get(parameterDto.getId());
			if(parameterDb == null) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PARAMETER_NOT_FOUND.val());
			}
			updateParameters(parameterDb, parameterDto,null,null);

		}
	}
	private void deleteParameters(List<Parameter> listParameterOld, List<Long> listIdParameterDelete) {
		listIdParameterDelete.forEach(id -> listParameterOld.stream()
				.filter(para -> para.getId().equals(id))
				.findFirst()
				.ifPresent(para -> para.setDeleted(true)));
	}
	private void addNewParameters(Category category, List<ParameterDto> listParameterNew) {
		listParameterNew.forEach(parameterDto -> {
			Parameter parameter = createParameter(parameterDto);
			category.addParameter(parameter);
		});
	}
	private void updateParameters(Parameter parameterDb, ParameterDto parameterDto, Category category, CategoryDto categoryDto) {
		if(parameterDb != null) {
			parameterDb.setName(parameterDto.getName());
			parameterDb.setGroupIndex(parameterDto.getGroupIndex());
			parameterDb.setUpdatedAt(LocalDateTime.now());
		}

		//nếu như paramterDto không null thì là cập nhật attribute trong paramter còn khôgn có thì là cập nhật attribute trong category
		List<AttributeDto> listAttributeDto = (parameterDto != null) ? parameterDto.getListAttributeDto() : categoryDto.getListAttributes();

		List<Attribute> listAttributeDb = (parameterDb != null) ? parameterDb.getListAttributes() : category.getListAttribute();


		List<Long> listIdAttributeOld = listAttributeDb.stream().map(Attribute::getId).toList();
		List<Long> listIdAttributeDto = listAttributeDto.stream().map(AttributeDto::getId).toList();

		// Danh sách các ID attribute cần xóa và thêm
		List<Long> listIdAttributeDelete = listIdAttributeOld.stream().filter(id -> !listIdAttributeDto.contains(id)).collect(Collectors.toList());
		deleteAttributes(listIdAttributeDelete,listAttributeDb);

		Map<Long, Attribute> mapAttributeDb = listAttributeDb.stream()
				.collect(Collectors.toMap(Attribute::getId, attr -> attr));

		// Update các attribute có ID trong danh sách
		updateAttributes(listAttributeDto,mapAttributeDb);

		// Thêm mới các attribute
		addNewAttributes(parameterDb, listAttributeDto,category);
	}

	//cập nhật attribute
	private void deleteAttributes(List<Long> listIdAttributeDelete,List<Attribute> listAttributeDb) {
		listIdAttributeDelete.forEach(id -> listAttributeDb.stream()
				.filter(attr -> attr.getId().equals(id))
				.findFirst()
				.ifPresent(attr -> attr.setDeleted(true)));
	}
	private void addNewAttributes(Parameter parameterDb, List<AttributeDto> listAttributeDto, Category category) {
		listAttributeDto.stream()
				.filter(attr -> attr.getId() == null)
				.forEach(attributeDto -> {
					Attribute attribute = createAttribute(attributeDto);
					if(category != null) {
						category.addAttribute(attribute);
					}
					else{
						parameterDb.addAttribute(attribute);
					}
				});
	}
	private void updateAttributes(List<AttributeDto> listAttributeDto, Map<Long, Attribute> mapAttributeDb) {
		for (AttributeDto attributeDto : listAttributeDto) {
			Attribute attribute = mapAttributeDb.get(attributeDto.getId());
			if(attribute == null) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ATTRIBUTE_NOT_FOUND.val());
			}
			AttributeUtils.updateAttribute(attribute,attributeDto);
		}

	}

	@Override
	@Transactional
	public CategoryResponse getCategoryById(Long id) {
		Category category=categoryRepository.findById(id).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_FOUND));
		Category categoryHasParameter=findNearestCategoryWithListParameter(category);
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setChildrens);
			mapper.map(src->src.getListBrand(),CategoryResponse::setListBrand);
		});

		return mapper.map(categoryHasParameter, CategoryResponse.class);
	}

	@Override
	@Transactional
	public List<CategoryResponse> getAll(){
		List<Category> categoryList = categoryRepository.findAll();
		List<CategoryResponse> CategoryResponseList = new ArrayList<>();
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setListAttribute);
			mapper.skip(CategoryResponse::setListParameter);
			mapper.map(source->source.getParentCategory().getId(),CategoryResponse::setParentId);
		});
		for(Category category : categoryList) {
			if(category.getParentCategory()!=null){
				continue;
			}
			CategoryResponse categoryResponse= mapper.map(category, CategoryResponse.class);
			if((category.getListParameter() !=null) && !category.getListParameter().isEmpty()){
				categoryResponse.setIsParameter(true);
			}
			CategoryResponseList.add(categoryResponse);
		}


		return CategoryResponseList;
	}

	//delete
	@Override
	public CategoryResponse deleteCategory(Long id) {
		Category categoryDelete =
				categoryRepository.findById(id).orElseThrow(
						() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.CATEGORY_NOT_FOUND));
		categoryDelete.setDeleted(true);
		softDeleteCategoryAndChildren(categoryDelete);
		categoryRepository.save(categoryDelete);

		return CategoryResponse.builder().id(categoryDelete.getId()).build();
	}

	private void softDeleteCategoryAndChildren(Category category) {
		// Đánh dấu xóa mềm Category hiện tại
		category.setDeleted(true);

		// Đệ quy cho các Category con
		if (category.getChildrens() != null) {
			for (Category child : category.getChildrens()) {
				softDeleteCategoryAndChildren(child);
			}
		}
	}

	@Override
	public void deleteCategories(Long[] ids) {
		List<Category> categories = categoryRepository.findAllById(Arrays.asList(ids));
		if (categories.size() != ids.length) {
			throw new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.CATEGORY_NOT_FOUND);
		}
		categories.forEach(category -> category.setDeleted(true));
		categoryRepository.saveAll(categories);

	}

	public Category getCategoryForCreateProduct(Long id){
		Category category=categoryRepository.findById(id).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_FOUND));
		return findNearestCategoryWithListParameter(category);
	}
	public Category findNearestCategoryWithListParameter(Category category) throws ExceptionOm {
		while (category != null) {
			if (category.getListParameter() != null && !category.getListParameter().isEmpty()) {
				return category;
			}
			// Nếu listParameter rỗng, tiếp tục truy lên category cha
			category = category.getParentCategory();
		}
		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NO_CATEGORY_WITH_LISTPARAMETER_FOUND);
	}

	public void AddListTypeProductToCategory(List<TypeProductDto> listTypeProductDto,Category category) {
		for(TypeProductDto typeProductDto:listTypeProductDto) {
			TypeProduct typeProduct=new TypeProduct();
			typeProduct.setName(typeProductDto.getName());
			typeProduct.setDeleted(false);
			typeProduct.setCreatedAt(LocalDateTime.now());
			category.addTypeProduct(typeProduct);
		}
	}
	public void DeleteListTypeProductFromCategory(List<TypeProduct> listTypeProductForDelete) {
		for (TypeProduct typeProduct : listTypeProductForDelete) {
				typeProduct.setDeleted(true);
				typeProduct.setUpdatedAt(LocalDateTime.now());
		}
	}

	public void UpdateListTypeProductToCategory(List<TypeProductDto> listTypeProductDto,List<TypeProduct> listTypeProduct) {
		for(TypeProductDto typeProductDto:listTypeProductDto) {
			TypeProduct typeProduct=listTypeProduct.stream().filter(t->t.getId().equals(typeProductDto.getId())).findFirst().get();
			typeProduct.setName(typeProductDto.getName());
			typeProduct.setUpdatedAt(LocalDateTime.now());
		}
	}
	@Override
	public List<CategoryResponse> getAllForAdmin(){
		List<Category> categoryList = categoryRepository.findAll();
		List<CategoryResponse> CategoryResponseList = new ArrayList<>();

		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setListAttribute);
			mapper.skip(CategoryResponse::setListParameter);
			mapper.skip(CategoryResponse::setListTypeProduct);
			mapper.skip(CategoryResponse::setListBrand);
			mapper.map(source->source.getParentCategory().getId(),CategoryResponse::setParentId);
			mapper.map(source->source.getChildrens(),CategoryResponse::setChildrens);
			mapper.map(src->src.getUrl(),CategoryResponse::setUrl);
		});
		for(Category category : categoryList) {
			if(category.getParentCategory()!=null){
				continue;
			}
			CategoryResponse categoryResponse= mapper.map(category, CategoryResponse.class);
			if((category.getListParameter() !=null) && !category.getListParameter().isEmpty()){
				categoryResponse.setIsParameter(true);
			}
			CategoryResponseList.add(categoryResponse);
		}
		for (CategoryResponse categoryResponse : CategoryResponseList) {
			// Gọi phương thức đệ quy để xử lý từng CategoryResponse và các CategoryResponse con
			processCategory(categoryResponse,categoryList);
		}


		return CategoryResponseList;
	}

	private void processCategory(CategoryResponse categoryResponse,List<Category> categoryList) {
		Category category=categoryList.stream().filter(c->c.getId().equals(categoryResponse.getId())).findFirst().orElse(null);
		// Kiểm tra nếu listParameter không null và không rỗng
		if (category.getListParameter() != null && !category.getListParameter().isEmpty()) {
			categoryResponse.setIsParameter(true);  // Đánh dấu là có parameter
		}
		else{
			categoryResponse.setIsParameter(false);
		}

		// Nếu có các CategoryResponse con (childrens), gọi đệ quy cho từng item
		if (categoryResponse.getChildrens() != null) {
			for (CategoryResponse child : categoryResponse.getChildrens()) {
				processCategory(child,categoryList);  // Đệ quy xử lý từng CategoryResponse con
			}
		}
	}

	@Override
	@Transactional
	public CategoryResponse getCategoryParameter(Long id) {
		log.info("hello1");
		Optional<Category> category=categoryRepository.findById(id);
		if(category.get().getListParameter()==null || category.get().getListParameter().isEmpty()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_PARAMTER);
		}
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setChildrens);
			mapper.map(src->src.getListAttribute(),CategoryResponse::setListAttribute);
			mapper.map(src->src.getListBrand(),CategoryResponse::setListBrand);
			mapper.map(src->src.getListTypeProduct(),CategoryResponse::setListTypeProduct);
			mapper.map(src->src.getListParameter(),CategoryResponse::setListParameter);
			mapper.map(src->src.getUrl(),CategoryResponse::setUrl);
		});
		CategoryResponse categoryResponse= mapper.map(category.get(), CategoryResponse.class);
		categoryResponse.getIsParameter();
		if(categoryResponse.getListParameter()!=null){
			for(ParameterResponse parameterResponse:categoryResponse.getListParameter()){
				for(AttributeResponse attributeResponse:parameterResponse.getListAttributes()){
					if(attributeResponse.getIsForVariant()){
						categoryResponse.getListAttribute().add(attributeResponse);
					}
				}
			}
			categoryResponse.getListAttribute();
		}


		return categoryResponse;
	}

	@Override
	@Transactional
	public CategoryResponse getCategoryParameterForUpdateParameter(Long id) {
		log.info("hello1");
		Optional<Category> category=categoryRepository.findById(id);
		if(category.get().getListParameter()==null || category.get().getListParameter().isEmpty()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_PARAMTER);
		}
		mapper.typeMap(Category.class, CategoryResponse.class).addMappings(mapper -> {
			mapper.skip(CategoryResponse::setChildrens);
			mapper.map(src->src.getListAttribute(),CategoryResponse::setListAttribute);
			mapper.map(src->src.getListBrand(),CategoryResponse::setListBrand);
			mapper.map(src->src.getListTypeProduct(),CategoryResponse::setListTypeProduct);
			mapper.map(src->src.getListParameter(),CategoryResponse::setListParameter);
			mapper.map(src->src.getUrl(),CategoryResponse::setUrl);
		});
		CategoryResponse categoryResponse= mapper.map(category.get(), CategoryResponse.class);
		categoryResponse.getIsParameter();
		if(categoryResponse.getListParameter()!=null){
			for(ParameterResponse parameterResponse:categoryResponse.getListParameter()){
				for(AttributeResponse attributeResponse:parameterResponse.getListAttributes()){
					if(attributeResponse.getIsForVariant()){
						categoryResponse.getListAttribute().add(attributeResponse);
					}
				}
			}
			categoryResponse.getListAttribute();
		}

		List<Long> listId=categoryResponse.getListAttribute().stream().map(AttributeResponse::getId).toList();
		List<Long> listIdd=categoryResponse.getListParameter().stream()
				.flatMap(parameter -> parameter.getListAttributes().stream())
				.map(attribute -> attribute.getId())
				.collect(Collectors.toList());

		List<Long> commonElements = listId.stream()
				.filter(listIdd::contains) // Lọc những phần tử có trong cả hai danh sách
				.collect(Collectors.toList());
		List<AttributeResponse> filteredAttributes = categoryResponse.getListAttribute().stream()
				.filter(attribute -> !commonElements.contains(attribute.getId())) // Loại bỏ các phần tử có ID trong commonElements
				.collect(Collectors.toList());

		categoryResponse.setListAttribute(filteredAttributes); // Nếu cần cập nhật lại danh sách trong `categoryResponse`


		return categoryResponse;
	}
}
