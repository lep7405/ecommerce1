package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.*;
import com.oms.service.app.dtos.Product.AttributeDto1;
import com.oms.service.app.dtos.Product.ParameterDto1;
import com.oms.service.app.dtos.Product.ProductDto;
import com.oms.service.app.response.*;
import com.oms.service.app.response.Review.ReviewResponse;
import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.Parameter;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.Product.Images;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.entities.Product.TypeProduct;
import com.oms.service.domain.enums.CommitmentEnum;
import com.oms.service.domain.enums.StateProduct;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.*;
import com.oms.service.domain.repositories.Filter.FilterItemRepository;
import com.oms.service.domain.services.CategoryService;
import com.oms.service.domain.services.ProductService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ProductVariantRepository productVariantRepository;
	private final AttributeRepository attributeRepository;
	private final AttributeValueRepository attributeValueRepository;
	private final FilterItemRepository filterItemRepository;

	private final CategoryService categoryService;
	private final ModelMapper mapper;
	private final ReviewRepository reviewRepository;

	private final TypeProductRepository typeProductRepository;
	private final BrandRepository brandRepository;
	@Override
	@Transactional
	public ProductResponse createProduct(ProductDto productDto)
	{
		//Validate
		Category category = categoryRepository.findById(productDto.getCategoryId()).orElseThrow(
				() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_NOT_FOUND.val() + productDto.getCategoryId()));

		validateImages(productDto.getImages());
		validateProductName(productDto.getName());

		//check brand
		Optional<Brand> brand = category.getListBrand().stream().filter(brand1 -> brand1.getId().equals(productDto.getBrandValueId())).findFirst();
		if(brand.isEmpty()) throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_BRAND.val());

		Optional<TypeProduct> typeProduct = category.getListTypeProduct().stream().filter(typeProduct1 -> typeProduct1.getId().equals(productDto.getTypeProductValueId())).findFirst();
		if(typeProduct.isEmpty()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_TYPE_PRODUCT.val());//check brand
		}

		Map<Long, Attribute> attributeMap = getAttributesFromParameterDto(productDto);
		Map<Long,Attribute> attributeMapVariant=new HashMap<>();
		if(productDto.getListProductVariantsDto().get(0).getListAttributeDto()!=null&&!productDto.getListProductVariantsDto().get(0).getListAttributeDto().isEmpty()){
			 attributeMapVariant = getAttributesFromVariantDto(productDto.getListProductVariantsDto());
			validateAttributeDtoInVariants(productDto.getListProductVariantsDto(),productDto,attributeMap,attributeMapVariant);
		}

		//validate
		validateParameters(productDto, category,attributeMap);

		boolean hasDuplicate = hasDuplicateCommitmentType(productDto.getListCommitment());
		if(hasDuplicate) throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.COMMITMENT_DUPLICATE_TYPE.val());


		//Create product
		Product product=new Product();
		addParametersToProduct(product, productDto,attributeMap);
		addProductVariantsToProduct(product, productDto.getListProductVariantsDto(), attributeMapVariant);
		// Các thiết lập cho product như name, category, images, brand, typeProduct, description, v.v.
		initializeProduct(product, productDto, category, brand.get(), typeProduct.get());
		product.setListCommitment(productDto.getListCommitment());
		productRepository.save(product);
		return convertToProductResponse(product);
	}
	@Override
	@Transactional
	public ProductResponse getProductDetail(Long id)
	{
		Product product=productRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND.val()+id));
		ProductResponse productResponse= convertToProductResponse(product);
		List<Review> reviewList=reviewRepository.findAllByProductId(product.getId());
		List<ReviewResponse> reviewResponseList=new ArrayList<>();
		for(Review review:reviewList){
			ReviewResponse reviewResponse=mapper.map(review, ReviewResponse.class);
			reviewResponseList.add(reviewResponse);
		}
		productResponse.setListReview(reviewResponseList);
		return productResponse;
	}
	@Override
	@Transactional
	public ProductResponse updateProduct(Long id, ProductDto productDto) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND.val()));

		if (!product.getName().equals(productDto.getName())) {
			validateProductName(productDto.getName());
			product.setName(productDto.getName());
		}
		Category category=product.getCategory();
		if(!Objects.equals(category.getId(), productDto.getCategoryId())){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_MUST_NOT_CHANGE.val());
		}
		if(!Objects.equals(product.getTypeProduct().getId(), productDto.getTypeProductValueId())){
			TypeProduct typeProduct=category.getListTypeProduct().stream().
					filter(typeProduct1 -> typeProduct1.getId().equals(productDto.getTypeProductValueId())).findFirst()
					.orElseThrow(()-> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_TYPE_PRODUCT.val()));
			product.setTypeProduct(typeProduct);
		}
		if(!Objects.equals(product.getBrand().getId(), productDto.getBrandValueId())){
			Brand brand=category.getListBrand().stream().
					filter(brand1 -> brand1.getId().equals(productDto.getBrandValueId())).findFirst()
					.orElseThrow(()-> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_BRAND.val()));
			product.setBrand(brand);
		}
		boolean hasDuplicate = hasDuplicateCommitmentType(productDto.getListCommitment());
		if(hasDuplicate) throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.COMMITMENT_DUPLICATE_TYPE.val());


		Map<Long, Attribute> attributeMap = getAttributesFromParameterDto(productDto);
		Map<Long,Attribute> attributeMapVariant=new HashMap<>();
		if(productDto.getListProductVariantsDto().get(0).getListAttributeDto()!=null&&!productDto.getListProductVariantsDto().get(0).getListAttributeDto().isEmpty()){
			attributeMapVariant = getAttributesFromVariantDto(productDto.getListProductVariantsDto());
			validateAttributeDtoInVariants(productDto.getListProductVariantsDto(),productDto,attributeMap,attributeMapVariant);
		}

		validateParameters(productDto, category,attributeMap);
		validateAttributeDtoInVariants(productDto.getListProductVariantsDto(), productDto, attributeMap, attributeMapVariant);

		// Xử lý tham số
		handleParameterDto(product, productDto);

		// Lấy danh sách ID của variants hiện có và DTO
		Set<Long> listIdVariantOld = product.getListProductVariants().stream()
				.map(ProductVariant::getId)
				.collect(Collectors.toSet());
		Set<Long> listIdVariantDto = productDto.getListProductVariantsDto().stream()
				.map(ProductVariantDto::getId)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		// Xóa các variants không còn trong DTO
		product.getListProductVariants().stream()
				.filter(variant -> !listIdVariantDto.contains(variant.getId()))
				.forEach(variant -> variant.setDeleted(true));

		// Thêm các variants mới
		List<ProductVariantDto> listProductVariantNewDto = productDto.getListProductVariantsDto().stream()
				.filter(variantDto -> variantDto.getId() == null)
				.collect(Collectors.toList());

		if (!listProductVariantNewDto.isEmpty()) {

			addProductVariantsToProduct(product, listProductVariantNewDto, getAttributesFromVariantDto(listProductVariantNewDto));
		}

		// Cập nhật các variants hiện có
		Set<Long> listVariantIdForUpdate = new HashSet<>(listIdVariantOld);
		listVariantIdForUpdate.retainAll(listIdVariantDto);

		Map<Long, ProductVariant> mapProductVariant = productVariantRepository.findAllById(listVariantIdForUpdate).stream()
				.collect(Collectors.toMap(ProductVariant::getId, variant -> variant));

		if (listVariantIdForUpdate.size() != mapProductVariant.size()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_PRODUCT_VARIANT.val());
		}

		// Lấy danh sách DTO cho các variants cần cập nhật
		List<ProductVariantDto> listProductVariantDtoForUpdate = productDto.getListProductVariantsDto().stream()
				.filter(variantDto -> listVariantIdForUpdate.contains(variantDto.getId()))
				.collect(Collectors.toList());

		Map<Long, Attribute> attributeMapVariantUpdate = getAttributesFromVariantDto(listProductVariantDtoForUpdate);

		for (ProductVariantDto variantDto : listProductVariantDtoForUpdate) {
			ProductVariant productVariant = mapProductVariant.get(variantDto.getId());

			// Tạo bản đồ Attribute ID -> AttributeDto
			Map<Long, AttributeDto1> dtoAttributesMap = variantDto.getListAttributeDto().stream()
					.collect(Collectors.toMap(AttributeDto1::getId, attrDto -> attrDto));

			// Tạo bản đồ Attribute ID -> RelVariantValueProduct hiện có
			Map<Long, RelVariantValueProduct> existingRelMap = productVariant.getListRelVariantValueProduct().stream()
					.collect(Collectors.toMap(
							rel -> rel.getAttributeValue().getAttribute().getId(),
							rel -> rel
					));

			Set<Long> dtoAttributeIds = dtoAttributesMap.keySet();
			Set<Long> currentAttributeIds = existingRelMap.keySet();

			// Xác định Attributes cần thêm, xóa, cập nhật
			Set<Long> attributesForDelete = new HashSet<>(currentAttributeIds);
			attributesForDelete.removeAll(dtoAttributeIds);

			Set<Long> attributesForAdd = new HashSet<>(dtoAttributeIds);
			attributesForAdd.removeAll(currentAttributeIds);

			Set<Long> attributesForUpdate = new HashSet<>(currentAttributeIds);
			attributesForUpdate.retainAll(dtoAttributeIds);

			// Xóa các RelVariantValueProduct không còn cần thiết
			attributesForDelete.forEach(idToDelete -> {
				RelVariantValueProduct rel = existingRelMap.get(idToDelete);
				if (rel != null) {
					rel.setDeleted(true);
				}
			});

			// Thêm mới RelVariantValueProduct cho Attributes cần thêm
			attributesForAdd.forEach(idToAdd -> {
				Attribute attribute = attributeMapVariantUpdate.get(idToAdd);
				AttributeDto1 attributeDto = dtoAttributesMap.get(idToAdd);
				Long valueId = attributeDto.getListAttributeValuesIds().get(0);

				AttributeValue attributeValue = attribute.getListAttributeValue().stream()
						.filter(av -> av.getId().equals(valueId))
						.findFirst()
						.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,
								ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + " id:" + valueId));

				RelVariantValueProduct newRel = new RelVariantValueProduct();
				newRel.setProductVariant(productVariant);
				newRel.setAttributeValue(attributeValue);
				productVariant.getListRelVariantValueProduct().add(newRel);
			});

			// Cập nhật RelVariantValueProduct cho Attributes cần cập nhật
			attributesForUpdate.forEach(idToUpdate -> {
				RelVariantValueProduct existingRel = existingRelMap.get(idToUpdate);
				AttributeDto1 attributeDto = dtoAttributesMap.get(idToUpdate);
				Attribute attribute = attributeMapVariantUpdate.get(idToUpdate);
				Long newValueId = attributeDto.getListAttributeValuesIds().get(0);

				AttributeValue newAttributeValue = attribute.getListAttributeValue().stream()
						.filter(av -> av.getId().equals(newValueId))
						.findFirst()
						.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,
								ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + " id:" + newValueId));

				if (!existingRel.getAttributeValue().getId().equals(newAttributeValue.getId())) {
					existingRel.setDeleted(true);

					RelVariantValueProduct updatedRel = new RelVariantValueProduct();
					updatedRel.setProductVariant(productVariant);
					updatedRel.setAttributeValue(newAttributeValue);
					productVariant.getListRelVariantValueProduct().add(updatedRel);
				}
			});
		}


		product.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		productRepository.save(product);

		return convertToProductResponse(product);
	}

	public void handleParameterDto(Product product, ProductDto productDto) {
		List<Long> listIdAttributeOld = product.getListRelVariantValueProduct().stream()
				.map(relVariantValue -> relVariantValue.getAttributeValue().getAttribute().getId())
				.toList();

		List<Long> listIdAttributeDto = productDto.getListParameterDto().stream()
				.flatMap(parameterDto -> parameterDto.getListAttributes().stream())
				.map(AttributeDto1::getId)
				.toList();

		// Lấy ra các attribute để xóa, cập nhật và thêm
		List<Long> listAttributeIdForParameterForDelete = listIdAttributeOld.stream()
				.filter(id -> !listIdAttributeDto.contains(id))
				.collect(Collectors.toList());

		List<Long> listAttributeIdForParameterForUpdate = listIdAttributeOld.stream()
				.filter(listIdAttributeDto::contains)
				.toList();

		List<Long> listAttributeIdForParameterForAdd = listIdAttributeDto.stream()
				.filter(id -> !listIdAttributeOld.contains(id))
				.toList();

		// Xóa các attribute
		removeOldRelVariantValueProducts(product, productDto, listAttributeIdForParameterForDelete);

		Map<Long, Attribute> attributeMap = getAttributesFromParameterDto(productDto);

		for (ParameterDto1 parameterDto : productDto.getListParameterDto()) {
			for (AttributeDto1 attributeDto : parameterDto.getListAttributes()) {
				// Lấy Attribute từ attributeMap theo ID
				Attribute attribute = attributeMap.get(attributeDto.getId());

				if (listAttributeIdForParameterForAdd.contains(attributeDto.getId())) {
					handleAddAttributeValue(product, attributeDto, attribute);
				} else if (listAttributeIdForParameterForUpdate.contains(attributeDto.getId())) {
					handleUpdateAttributeValue(product, attributeDto, attribute);
				}
			}
		}
	}
	private void handleAddAttributeValue(Product product, AttributeDto1 attributeDto, Attribute attribute) {
		// Kiểm tra nếu Attribute không bắt buộc và không có giá trị nào được cung cấp
		if (!attribute.getIsRequired() && (attributeDto.getListAttributeValuesIds().isEmpty() && attributeDto.getAttributeValue() == null)) {
			return;
		}

		// Nếu có attirbuteValueId gửi lên
		if (!attributeDto.getListAttributeValuesIds().isEmpty()) {
			for (Long attributeValueId : attributeDto.getListAttributeValuesIds()) {
				AttributeValue attributeValue = attribute.getListAttributeValue().stream().filter(av -> av.getId().equals(attributeValueId))
						.findFirst()
						.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val()+" "+ attributeValueId));
				createRelVariantValueProduct(product, attributeValue);
			}
		} else if(attributeDto.getAttributeValue() != null) {
			AttributeValue attributeValue = new AttributeValue();
			AttValue attValue = new AttValue();
			attValue.setAttValueString(attributeDto.getAttributeValue());
			attributeValue.setDeleted(false);
			attributeValue.setCreatedAt(LocalDateTime.now());
			attributeValue.setAttValue(attValue);
			createRelVariantValueProduct(product, attributeValue);
		}
	}
	private void handleUpdateAttributeValue(Product product, AttributeDto1 attributeDto, Attribute attribute) {
		// Kiểm tra nếu Attribute không bắt buộc và không có giá trị nào được cung cấp
		if (!attribute.getIsRequired() && (attributeDto.getListAttributeValuesIds().isEmpty() || attributeDto.getAttributeValue() == null)) {
			return;
		}

		if (attribute.getIsSelect()) {
			updateSelectedAttributeValue(product, attributeDto, attribute);
		} else {
			updateNonSelectedAttributeValue(attributeDto, attribute,product);
		}
	}
	private void updateSelectedAttributeValue(Product product, AttributeDto1 attributeDto, Attribute attribute) {
		List<Long> listAttributeValueIds = attributeDto.getListAttributeValuesIds();
		List<AttributeValue> currentAttributeValues = product.getListRelVariantValueProduct().stream()
				.map(RelVariantValueProduct::getAttributeValue)
				.filter(attributeValue -> attributeValue.getAttribute().getId().equals(attributeDto.getId()))
				.collect(Collectors.toList());

		// Tìm ID cần xóa và cần thêm
		List<Long> currentAttributeValueIds = currentAttributeValues.stream().map(AttributeValue::getId).toList();
		List<Long> idsToDelete = currentAttributeValueIds.stream().filter(id -> !listAttributeValueIds.contains(id)).toList();
		List<Long> idsToAdd = listAttributeValueIds.stream().filter(id -> !currentAttributeValueIds.contains(id)).toList();

		// Xóa các RelVariantValueProduct không còn tồn tại
		for (Long id : idsToDelete) {
			AttributeValue attributeValueToDelete = findAttributeValueById(currentAttributeValues, id);
			RelVariantValueProduct relVariantValueProduct = attributeValueToDelete.getListRelVariantValueProduct()
					.stream()
					.filter(rel -> rel.getProduct().getId().equals(product.getId()))
					.findFirst()
					.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_REL_VARIANT_VALUE_PRODUCT.val()));

			relVariantValueProduct.setDeleted(true);
		}

		// Thêm các RelVariantValueProduct mới
		for (Long id : idsToAdd) {
			AttributeValue attributeValueToAdd = findAttributeValueById(currentAttributeValues, id);
			createRelVariantValueProduct(product, attributeValueToAdd);
		}
	}
	private void updateNonSelectedAttributeValue(AttributeDto1 attributeDto, Attribute attribute, Product product) {
		if(attributeDto.getListAttributeValuesIds()!=null&& !attributeDto.getListAttributeValuesIds().isEmpty()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + attributeDto.getId());
		}
		AttributeValue attributeValue= attribute.getListAttributeValue().stream()
				.flatMap(attributeValue1 -> attributeValue1.getListRelVariantValueProduct().stream())
				.filter(relVariantValueProduct -> relVariantValueProduct.getProduct().getId().equals(product.getId()))
				.map(RelVariantValueProduct::getAttributeValue)
				.findFirst()
				.orElse(null);
		if(attribute.getIsRequired()&&attributeValue==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + attributeDto.getId());
		}
		if(attributeValue!=null&&attributeDto.getAttributeValue() != null){
			if(!attributeValue.getAttValue().getAttValueString().equals(attributeDto.getAttributeValue())){
				RelVariantValueProduct relVariantValueProductOld = attributeValue.getListRelVariantValueProduct()
						.stream()
						.filter(rel -> rel.getProduct().getId().equals(product.getId()))
						.findFirst()
						.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_REL_VARIANT_VALUE_PRODUCT.val()));
				relVariantValueProductOld.setDeleted(true);
				attributeValue.setUpdatedAt(LocalDateTime.now());
				attributeValue.setDeleted(true);


				AttValue attValue = new AttValue();
				attValue.setAttValueString(attributeDto.getAttributeValue());
				AttributeValue attributeValueNew= new AttributeValue();
				attributeValueNew.setDeleted(false);
				attributeValueNew.setCreatedAt(LocalDateTime.now());
				attributeValueNew.setAttValue(attValue);
				attribute.addAttributeValue(attributeValueNew);
				attributeValueRepository.save(attributeValueNew);

				RelVariantValueProduct relVariantValueProduct = new RelVariantValueProduct();
				relVariantValueProduct.setDeleted(false);

				attributeValueNew.addRelVariantValueProduct(relVariantValueProduct);

				product.addRelVariantValueProduct(relVariantValueProduct);



			}
		}
	}
	private AttributeValue findAttributeValueById(List<AttributeValue> attributeValues, Long id) {
		return attributeValues.stream()
				.filter(av -> av.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val()));
	}
	private void createRelVariantValueProduct(Product product, AttributeValue attributeValue) {
		RelVariantValueProduct relVariantValueProduct = new RelVariantValueProduct();
		relVariantValueProduct.setDeleted(false);
		attributeValue.addRelVariantValueProduct(relVariantValueProduct);
		product.addRelVariantValueProduct(relVariantValueProduct);
	}



	public void validateImages(List<Images> images) {
		if (images == null || images.isEmpty()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.IMAGES_NOT_FOUND.val());
		}
		long coverCount = images.stream()
				.filter(Images::getIsCover)
				.count();
		if (coverCount != 1) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_COVER_IMAGE.val());
		}
	}

	private void validateProductName(String name) {
		Optional<Product> productCheckName = productRepository.findByName(name);
		if (productCheckName.isPresent()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NAME_ALREADY_EXISTS.val() + name);
		}
	}

	private void validateParameters(ProductDto productDto, Category categoryWithParameters ,Map<Long, Attribute> attributeMap) {
		List<Long> listIdParameterForCategory = categoryWithParameters.getListParameter()
				.stream().map(Parameter::getId).toList();

		List<Long> listIdParameterDto = productDto.getListParameterDto().stream().map(ParameterDto1::getId).toList();

		//check xem đủ parameter
		if (listIdParameterForCategory.size() != listIdParameterDto.size() ||! listIdParameterForCategory.containsAll(listIdParameterDto)) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_PARAMETER.val() + listIdParameterForCategory);
		}
		//check attribute
		for(ParameterDto1 parameterDto:productDto.getListParameterDto()){
			for(AttributeDto1 attributeDto:parameterDto.getListAttributes()){
				validateAttributeDto(attributeDto,attributeMap);
			}
		}
	}
	public void validateAttributeDto(AttributeDto1 attributeDto, Map<Long, Attribute> attributeMap){

		Attribute attribute=attributeMap.get(attributeDto.getId());
		boolean isSelect = attribute.getIsSelect();
		boolean isSelectMultiple = attribute.getIsSelectMultiple();
		boolean isRequired = attribute.getIsRequired();

		List<Long> listIdAttributeValue = attribute.getListAttributeValue().stream()
				.map(AttributeValue::getId)
				.toList();

		List<Long> listIdAttributeDto = attributeDto.getListAttributeValuesIds();
		String attributeValue = attributeDto.getAttributeValue();

		int attributeValueSize = listIdAttributeDto.size();
		if ((isSelect && attributeValueSize == 0) ||
				(!isSelect && attributeValueSize > 0) ||
				(isRequired && attributeValueSize == 0 && attributeValue == null) ||
				(!isSelectMultiple && attributeValueSize > 1) ||
				(!listIdAttributeValue.containsAll(listIdAttributeDto) && isSelect)
		) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + attributeDto.getId());
		}
	}
	public void validateAttributeDtoInVariants(
			List<ProductVariantDto> listProductVariantDto,
			ProductDto productDto,
			Map<Long, Attribute> attributeMap,
			Map<Long, Attribute> attributeVariantMap) {
		// Kiểm tra từng thuộc tính trong biến thể có phải là "for variant"
		Set<Long> referenceAttributeIds = new HashSet<>();
		Set<String> variantSet = new HashSet<>();

		for (int i = 0; i < listProductVariantDto.size(); i++) {
			ProductVariantDto variantDto = listProductVariantDto.get(i);

			Set<Long> currentAttributeIds = variantDto.getListAttributeDto().stream()
					.map(AttributeDto1::getId)
					.collect(Collectors.toSet());
			// Kiểm tra xem nó các variant nó có cùng 1 bộ attribute không
			if (i == 0) {
				referenceAttributeIds = currentAttributeIds;
			} else if (!currentAttributeIds.equals(referenceAttributeIds)) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_FOR_VARIANTS.val()+"1 "+currentAttributeIds);
			}

			// Kiểm tra xem các biến thể có trùng lặp tất cả thuộc tính không
			String variantKey = variantDto.getListAttributeDto().stream()
					.sorted(Comparator.comparing(AttributeDto1::getId))
					.flatMap(attrDto -> attrDto.getListAttributeValuesIds().stream())
					.sorted()
					.map(String::valueOf)
					.collect(Collectors.joining(","));

			if (!variantSet.add(variantKey)) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_VARIANT.val() + "2 " + variantKey);
			}

			for (AttributeDto1 attributeDto : variantDto.getListAttributeDto()) {
				Attribute attribute = attributeMap.get(attributeDto.getId());

//				if (attribute == null || !attribute.getIsForVariant() || !attribute.getIsSelect() ) {
//					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_FOR_VARIANTS.val()+" "+attributeDto.getId());
//				}
				// Kiểm tra xem attribute của variant có giá trị hợp lệ trong product hay không -trường hợp attribute của biến thể trùng với attribute product
				Long attributeId = attributeDto.getId();
				List<Long> variantValues = attributeDto.getListAttributeValuesIds();
				Set<Long> validValues = productDto.getListParameterDto().stream()
						.flatMap(param -> param.getListAttributes().stream())
						.filter(attr -> attributeMap.get(attr.getId()) != null && attributeMap.get(attr.getId()).getIsForVariant())
						.filter(attr -> attr.getId().equals(attributeId))
						.findFirst()
						.map(AttributeDto1::getListAttributeValuesIds)
						.map(HashSet::new)
						.orElse(null);

				if (validValues != null) {
					if (!validValues.containsAll(variantValues)||validValues.size()!=productDto.getListProductVariantsDto().size()) {
						throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NO_MATCH_VALUE_IN_VARIANT_AND_PRODUCT+" "+attributeId);
					}
				}

			}
		}
		// Kiểm tra giá trị trong từng AttributeDto của biến thể
		for (ProductVariantDto variantDto : listProductVariantDto) {
			for (AttributeDto1 attributeDto : variantDto.getListAttributeDto()) {
				validateAttributeValueDtoInVariant(attributeDto, attributeVariantMap);
			}
		}
	}
	public void validateAttributeValueDtoInVariant(AttributeDto1 attributeDto, Map<Long, Attribute> attributeMap){
		Attribute attribute=attributeMap.get(attributeDto.getId());
		List<Long> listIdAttributeValue=attribute.getListAttributeValue().stream().map(AttributeValue::getId).toList();
		if(attributeDto.getListAttributeValuesIds().size() != 1 || !listIdAttributeValue.containsAll(attributeDto.getListAttributeValuesIds())
		){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE_VALUE.val() + attributeDto.getId());
		}
	}

	//valid và lấy tất cả attribute db tương ứng với các attribute id gửi lên
	private Map<Long, Attribute> getAttributesFromParameterDto(ProductDto productDto) {
		List<Long> listAttributeIdsDto = productDto.getListParameterDto().stream()
				.flatMap(parameterDto -> parameterDto.getListAttributes().stream())
				.map(AttributeDto1::getId)
				.collect(Collectors.toList());
		Map<Long, Attribute> attributeMap= attributeRepository.findAllById(listAttributeIdsDto).stream()
				.collect(Collectors.toMap(Attribute::getId, attribute -> attribute));
		if(attributeMap.size()!=listAttributeIdsDto.size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE.val() + listAttributeIdsDto);
		}
		return attributeMap;
	}


	private Map<Long, Attribute> getAttributesFromVariantDto(List<ProductVariantDto> listProductVariantDtos) {
		//-

		List<List<Long>> listOfAttributeLists = new ArrayList<>();
		for (ProductVariantDto productVariantDto :listProductVariantDtos) {
			List<Long> listIdAttributeForEachProductVariant = productVariantDto.getListAttributeDto()
					.stream()
					.map(AttributeDto1::getId)
					.collect(Collectors.toList());
			listOfAttributeLists.add(listIdAttributeForEachProductVariant);
		}

		List<Long> listAttributeIdsDto = listProductVariantDtos.stream()
				.flatMap(productVariantDto -> productVariantDto.getListAttributeDto().stream())
				.map(AttributeDto1::getId)
				.toList();
		Map<Long, Attribute> attributeMap= attributeRepository.findAllById(listOfAttributeLists.get(0)).stream()
				.collect(Collectors.toMap(Attribute::getId, attribute -> attribute));
		if(attributeMap.size()!=listOfAttributeLists.get(0).size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ERROR_ATTRIBUTE.val() + listAttributeIdsDto);
		}
		return attributeMap;
	}
	public void addParametersToProduct(Product product, ProductDto productDto, Map<Long, Attribute> attributeMap) {
		for(ParameterDto1 parameterDto : productDto.getListParameterDto()) {
			for (AttributeDto1 attributeDto : parameterDto.getListAttributes()) {
				Attribute attribute = attributeMap.get(attributeDto.getId());
//				if(attribute.getIsForVariant()&&attributeDto.getListAttributeValuesIds().size()!=productDto.getListProductVariantsDto().size()){
//					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_ISFORVARIANT_IN_PARAMTER);
//				}
				//nếu attribute mà không bắt buộc với không phải isSelect thì có thể không gửi các attributeValue lên
				if (!attribute.getIsRequired()&&!attribute.getIsSelect() && (attributeDto.getListAttributeValuesIds().isEmpty() &&attributeDto.getAttributeValue()==null)) {
					continue;
				}
				if (!attributeDto.getListAttributeValuesIds().isEmpty()) {
					for (Long attributeValueId : attributeDto.getListAttributeValuesIds()) {
						AttributeValue attributeValue = attribute.getListAttributeValue().stream().filter(attributeValue1 -> attributeValue1.getId().equals(attributeValueId)).findFirst().get();
						RelVariantValueProduct relVariantValueProduct = new RelVariantValueProduct();
						relVariantValueProduct.setDeleted(false);
						relVariantValueProduct.setCreatedAt(LocalDateTime.now());
						attributeValue.addRelVariantValueProduct(relVariantValueProduct);
						product.addRelVariantValueProduct(relVariantValueProduct);
					}
				} else {
					AttributeValue attributeValue = new AttributeValue();
					AttValue attValue = new AttValue();
					attValue.setAttValueString(attributeDto.getAttributeValue());
					attributeValue.setDeleted(false);
					attributeValue.setCreatedAt(LocalDateTime.now());
					attributeValue.setAttValue(attValue);

					RelVariantValueProduct relVariantValueProduct = new RelVariantValueProduct();
					relVariantValueProduct.setDeleted(false);
					relVariantValueProduct.setCreatedAt(LocalDateTime.now());
					attributeValue.addRelVariantValueProduct(relVariantValueProduct);
					attribute.addAttributeValue(attributeValue);

					product.addRelVariantValueProduct(relVariantValueProduct);
				}

			}
		}

	}
	private void addProductVariantsToProduct(Product product,List<ProductVariantDto> listProductVariantDto, Map<Long, Attribute> attributeMap) {

		for (ProductVariantDto productVariantDto : listProductVariantDto) {
			ProductVariant productVariant = new ProductVariant();
			if(!attributeMap.isEmpty()){
				for (AttributeDto1 attributeDto : productVariantDto.getListAttributeDto()) {
					Attribute attribute = attributeMap.get(attributeDto.getId());

					attribute.getListAttributeValue().stream()
							.filter(attributeValue -> attributeValue.getId().equals(attributeDto.getListAttributeValuesIds().get(0)))
							.findFirst()
							.ifPresent(attributeValue -> {
								RelVariantValueProduct relVariantValueProduct = new RelVariantValueProduct();
								relVariantValueProduct.setDeleted(false);
								attributeValue.addRelVariantValueProduct(relVariantValueProduct);
								productVariant.addRelVariantValueProduct(relVariantValueProduct);
							});
				}
			}
			productVariant.setCreatedAt(LocalDateTime.now());
			productVariant.setDeleted(false);
			productVariant.setQuantity(productVariantDto.getQuantity());
			productVariant.setImages(productVariantDto.getImage());
			productVariant.setPrice(productVariantDto.getPrice());
			product.addProductVariant(productVariant);
		}
	}
	public void initializeProduct(Product product,ProductDto productDto, Category category, Brand brand, TypeProduct typeProduct) {
		product.setName(productDto.getName());
		product.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		product.setDeleted(false);
		product.setImages(productDto.getImages());
		product.setMax1Buy(productDto.getMax1Buy());
		product.setState(StateProduct.PENDING);
		product.setDescription(productDto.getDescription());
		AverageReview averageReview=AverageReview.builder().numberOf1stars(0).numberOf2stars(0).numberOf3stars(0).numberOf4stars(0).numberOf5stars(0).build();
		product.setAverageReview(averageReview);

		product.setMaxPrice(productDto.getListProductVariantsDto().stream()
				.map(ProductVariantDto::getPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
		product.setMinPrice(productDto.getListProductVariantsDto().stream()
				.map(ProductVariantDto::getPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));

		product.setBrand(brand);
		product.setTypeProduct(typeProduct);
		product.setCategory(category);
	}
	private void removeOldRelVariantValueProducts(Product product,ProductDto productDto, List<Long> listAttributeIdForParameterForDelete) {
		Map<Long, Attribute> attributeMapOld = product.getListRelVariantValueProduct().stream()
				.map(relVariantValueProduct -> relVariantValueProduct.getAttributeValue().getAttribute())
				.distinct()
				.collect(Collectors.toMap(Attribute::getId, attribute -> attribute));

		for (Long idAttribute : listAttributeIdForParameterForDelete) {
			Attribute attribute = attributeMapOld.get(idAttribute);

			List<RelVariantValueProduct> listRel = attribute.getListAttributeValue().stream()
					.flatMap(attributeValue -> attributeValue.getListRelVariantValueProduct().stream()
							.filter(relVariantValueProduct -> relVariantValueProduct.getProduct().getId().equals(product.getId())))
					.toList();

			for (RelVariantValueProduct relVariantValueProduct : listRel) {
				relVariantValueProduct.setDeleted(true);
			}
			productDto.getListParameterDto().forEach(parameterDto ->
					parameterDto.getListAttributes().removeIf(attributeDto ->
							Objects.equals(attributeDto.getId(), idAttribute))
			);
		}
	}

	public ProductResponse convertToProductResponse(Product product) {
		Category category=product.getCategory();
		ProductResponse productResponse=ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.images(product.getImages())
				.minPrice(product.getMinPrice())
				.maxPrice(product.getMaxPrice())
				.max1Buy(product.getMax1Buy())
				.state(product.getState())
				.categoryId(category.getId())
				.categoryName(category.getName())
				.build();
		List<ProductVariantResponse> listProductVariantResponse=new ArrayList<>();

		List<ParameterResponse> listParameterResponse=new ArrayList<>();

		for(Parameter parameter:category.getListParameter()){
			ParameterResponse parameterResponse=ParameterResponse.builder()
					.id(parameter.getId())
					.name(parameter.getName())
					.groupIndex(parameter.getGroupIndex())
					.build();
			List<AttributeResponse> listAttributeResponse=new ArrayList<>();

			for(Attribute attribute:parameter.getListAttributes()){
				AttributeResponse attributeResponse=AttributeResponse.builder()
						.id(attribute.getId())
						.name(attribute.getName())
						.dataType(attribute.getDataType())
						.isSelect(attribute.getIsSelect())
						.isSelectMultiple(attribute.getIsSelectMultiple())
						.build();

				if(attribute.getIsSelect()){
					List<AttributeValueResponse> attributeValueResponseList=new ArrayList<>();
					for(AttributeValue attributeValue:attribute.getListAttributeValue()){
						AttributeValueResponse attributeValueResponse=AttributeValueResponse.builder()
								.id(attributeValue.getId())
								.attValueString(attributeValue.getAttValue().getAttValueString())
								.build();
						attributeValueResponseList.add(attributeValueResponse);
					}
					attributeResponse.setListAttributeValueCategory(attributeValueResponseList);
				}


				List<RelVariantValueProduct> listRelVariantValueProduct = attribute.getListAttributeValue().stream()
						.flatMap(attributeValue -> attributeValue.getListRelVariantValueProduct().stream())
						.filter(rel -> rel.getProduct() != null && rel.getProduct().getId() != null && rel.getProduct().getId().equals(product.getId()))
						.toList();
				if(listRelVariantValueProduct==null){
					continue;
				}
				List<AttributeValue> listAttributeValue= listRelVariantValueProduct.stream()
						.map(RelVariantValueProduct::getAttributeValue)
						.toList();
				{
					List<AttributeValueResponse> listAttributeValueResponse = new ArrayList<>();

					for (AttributeValue attributeValue : attribute.getIsSelect() ? listAttributeValue : List.of(listAttributeValue.get(0))) {
						AttributeValueResponse attributeValueResponse = AttributeValueResponse.builder()
								.id(attributeValue.getId())
								.attValueString(attributeValue.getAttValue().getAttValueString())
								.build();
						listAttributeValueResponse.add(attributeValueResponse);
					}
					if(attribute.getIsSelect()){
						attributeResponse.setListAttributeValue(listAttributeValueResponse);

						if (!listAttributeValueResponse.isEmpty()) {
							listAttributeResponse.add(attributeResponse);
						}
					}
					else{
						AttributeValueResponse attributeValueResponse=listAttributeValueResponse.get(0);
						attributeResponse.setAttributeValue(attributeValueResponse);
						listAttributeResponse.add(attributeResponse);
					}


				}
				parameterResponse.setListAttributes(listAttributeResponse);

			}
			listParameterResponse.add(parameterResponse);
		}
		productResponse.setListRelVariantValueProduct(listParameterResponse);

		for(ProductVariant productVariant:product.getListProductVariants()){
			ProductVariantResponse productVariantResponse=ProductVariantResponse.builder()
					.id(productVariant.getId())
					.price(productVariant.getPrice())
					.quantity(productVariant.getQuantity())
					.image(productVariant.getImages())
					.build();
			List<AttributeResponse> listAttributeResponse=new ArrayList<>();
			if(productVariant.getListRelVariantValueProduct()!=null&&!productVariant.getListRelVariantValueProduct().isEmpty()){
				for(RelVariantValueProduct relVariantValueProduct:productVariant.getListRelVariantValueProduct()){
					AttributeValue attributeValue=relVariantValueProduct.getAttributeValue();

					AttributeValueResponse attributeValueResponse=AttributeValueResponse.builder()
							.id(attributeValue.getId())
							.attValueString(attributeValue.getAttValue().getAttValueString())
							.build();
					List<AttributeValueResponse> listAttributeResponse1 = new ArrayList<>();

					listAttributeResponse1.add(attributeValueResponse);
					Attribute attribute=attributeValue.getAttribute();
					AttributeResponse attributeResponse=AttributeResponse.builder()
							.id(attribute.getId())
							.name(attribute.getName())
							.dataType(attribute.getDataType())
							.listAttributeValue(listAttributeResponse1)
							.build();
					listAttributeResponse.add(attributeResponse);

				}
			}

			productVariantResponse.setListAttributes(listAttributeResponse);
			listProductVariantResponse.add(productVariantResponse);
		}
		productResponse.setListProductVariants(listProductVariantResponse);
		productResponse.setAverageReview(product.getAverageReview());
		BrandResponse brandResponse=BrandResponse.builder().id(product.getBrand().getId()).name(product.getBrand().getName()).build();
		TypeProductResponse typeProductResponse=new TypeProductResponse();
		typeProductResponse.setId(product.getTypeProduct().getId());
		typeProductResponse.setName(product.getTypeProduct().getName());
		productResponse.setTypeProduct(typeProductResponse);
		productResponse.setBrand(brandResponse);
		return productResponse;
	}

//	public ProductResponse convert (Product product){
//		mapper.typeMap(Product.class, ProductResponse.class).addMappings(mapper -> {
//			mapper.map(Product::getListRelVariantValueProduct,ProductResponse::setListRelVariantValueProduct);
//		});
//		mapper.typeMap(RelVariantValueProduct.class, ParameterResponse.class).addMappings(mapper -> {
//			mapper.map(source -> source.getAttributeValue().getAttribute().getParameter().getId(), ParameterResponse::setId);
//			mapper.map(source -> source.getAttributeValue().getAttribute().getParameter().getName(), ParameterResponse::setName);
//			mapper.map(source -> source.getAttributeValue().getAttribute(), ParameterResponse::setListAttributes);
//		});
//		mapper.typeMap(AttributeValue.class, AttributeResponse.class).addMappings(mapper -> {
//			mapper.map(source->source.getAttribute().getName(),AttributeResponse::setName);
//			mapper.map(source->source.getAttribute().getId(),AttributeResponse::setId);
//			mapper.map(source->source.getAttribute().getIsRequired(),AttributeResponse::setIsRequired);
//		});
//	}

//	@Override
//	@Transactional
//	public Page<Product> getAllProducts(FilterDto filterDto,Pageable pageable) {
//		Page<Product> productPages =
//				productRepository.filterProduct(
//						filterDto.getName(),
//						filterDto.getCategoryId(),
//						filterDto.getMinPrice(),
//						filterDto.getMaxPrice(),
//						filterDto.getListAttributeValueId(),
//						filterDto.getListAttributeValueId().size(),
//						pageable);
//		return productPages;
//	}
	@Override
	@Transactional
	public Page<Product> getAllProducts(FilterDto filterDto,Pageable pageable) {
		filterDto.setName(null);
		Page<Product> productPages =
				productRepository.filterProducts(
						filterDto.getMinPrice(),
						filterDto.getMaxPrice(),
						filterDto.getStateProduct(),
						filterDto.getName(),
						filterDto.getBrandId(),
						filterDto.getTypeProductId(),
						filterDto.getCategoryId(),
						filterDto.getListFilterItemId(),
						pageable);
		return productPages;
	}

	@Override
	@Transactional
	public ProductResponse deleteProduct(Long id) {
		Product product =
				productRepository
						.findById(id)
						.orElseThrow(
								() -> {
									log.error(" Product not found");
									return new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND);
								});
		product.setDeleted(true);
		product.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		productRepository.save(product);
		return ProductResponse.builder().id(product.getId()).build();
	}

	@Override
	@Transactional
	public void deleteProducts() {
		productRepository.updateAllProductsToDeleted();
	}

	@Override
	public List<ProductResponeSimpleResponse> getAllProducts(Long categoryId) {
		List<Product> listProduct=productRepository.findAllByCategoryAndSubcategories(categoryId);
		List<ProductResponeSimpleResponse> listProductResponeSimpleResponse=new ArrayList<>();
		for(Product product:listProduct){
			ProductResponeSimpleResponse productResponeSimpleResponse=new ProductResponeSimpleResponse();
			productResponeSimpleResponse.setId(product.getId());
			productResponeSimpleResponse.setName(product.getName());
			productResponeSimpleResponse.setUrl(product.getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
			productResponeSimpleResponse.setMinPrice(product.getMinPrice());
			productResponeSimpleResponse.setMaxPrice(product.getMaxPrice());
			listProductResponeSimpleResponse.add(productResponeSimpleResponse);
		}
		return listProductResponeSimpleResponse;
	}

	private boolean hasDuplicateCommitmentType(List<Commitment> commitments) {
		if (commitments == null || commitments.isEmpty()) {
			return false;
		}

		Set<CommitmentEnum> seenTypes = new HashSet<>();
		for (Commitment c : commitments) {
			if (!seenTypes.add(c.getType())) { // Thêm type vào set, nếu trùng sẽ trả về false
				return true;
			}
		}
		return false;
	}

}





