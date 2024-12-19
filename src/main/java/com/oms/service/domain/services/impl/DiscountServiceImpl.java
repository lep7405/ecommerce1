package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Discount.DiscountCombo.DiscountComboDto;
import com.oms.service.app.dtos.Discount.DiscountCombo.ProgramDiscountComboDto;
import com.oms.service.app.dtos.Discount.DiscountGift.DiscountGiftDto;
import com.oms.service.app.dtos.Discount.DiscountGift.ProgramDiscountGiftDto;
import com.oms.service.app.dtos.Discount.DiscountHotSaleIDto.DiscountHotSaleDto;
import com.oms.service.app.dtos.Discount.DiscountHotSaleIDto.ProgramDiscountHotSaleDto;
import com.oms.service.app.response.DiscountCombo.DiscountComboResponse;
import com.oms.service.app.response.DiscountCombo.ProgramDiscountResponse;
import com.oms.service.app.response.DiscountCombo1.DiscountCombo1Response;
import com.oms.service.app.response.DiscountCombo1.ProgramDiscountCombo1Response;
import com.oms.service.app.response.DiscountHotSale.DiscountHotSaleResponse;
import com.oms.service.app.response.DiscountHotSale.ProgramDiscountHotSaleResponse;
import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.Discount.Discount;
import com.oms.service.domain.entities.Discount.ProgramDiscount;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.entities.RelDiscountProduct;
import com.oms.service.domain.enums.DiscountType;
import com.oms.service.domain.enums.ProgramDiscountType;
import com.oms.service.domain.enums.ProgramType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.CategoryRepository;
import com.oms.service.domain.repositories.Discount.ProgramDiscountRepository;
import com.oms.service.domain.repositories.ProductRepository;
import com.oms.service.domain.repositories.ProductVariantRepository;
import com.oms.service.domain.services.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
	private final ProductRepository productRepository;
	private final ProductVariantRepository productVariantRepository;
	private final CategoryRepository categoryRepository;
	private final ProgramDiscountRepository programDiscountRepository;
	private final ModelMapper modelMapper;

	@Override
	@Transactional
	public ProgramDiscount createDiscountHotSale(ProgramDiscountHotSaleDto programDiscountHotSaleDto) {
		ProgramDiscount programDiscountCheckedName=programDiscountRepository.findByName(programDiscountHotSaleDto.getName());
		if(programDiscountCheckedName!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PROGRAM_DISCOUNT_NAME_ALREADY_EXISTS.val());
		}
		// Validate
		// Kiểm tra giá trị của programType và validate các trường tùy thuộc vào điều kiện

		List<Discount> listDiscount=new ArrayList<>();
		if (programDiscountHotSaleDto.getProgramType() == ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORIES) {
			for(DiscountHotSaleDto discountHotSaleDto:programDiscountHotSaleDto.getListDiscountHotSaleDto()) {
				if(discountHotSaleDto.getDiscountType()==DiscountType.PERCENTAGE){
					if(discountHotSaleDto.getMaximumAmount()==null||discountHotSaleDto.getMinimumAmount()==null||discountHotSaleDto.getDiscountPercentage()==null){
						throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.LACK_MAX_MIN_PERCENTAGE_AMOUNT);
					}
				}
				else if(discountHotSaleDto.getDiscountType()==DiscountType.VALUE){
					if(discountHotSaleDto.getDiscountAmount()==null){
						throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISCOUNT_AMOUNT_REQUIRED);

					}
				}
			}
		}
		if (programDiscountHotSaleDto.getProgramType() == ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORY) {
			for (DiscountHotSaleDto discountHotSaleDto : programDiscountHotSaleDto.getListDiscountHotSaleDto()) {
				if (discountHotSaleDto.getCategoryId() == null) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_ID_REQUIRED);
				}
				if (discountHotSaleDto.getDiscountType() == DiscountType.PERCENTAGE) {
					if (discountHotSaleDto.getMaximumAmount() == null || discountHotSaleDto.getMinimumAmount() == null || discountHotSaleDto.getDiscountPercentage() == null) {
						throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.LACK_MAX_MIN_PERCENTAGE_AMOUNT);
					}
				} else if (discountHotSaleDto.getDiscountType() == DiscountType.VALUE) {
					if (discountHotSaleDto.getDiscountAmount() == null) {
						throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISCOUNT_AMOUNT_REQUIRED);

					}
				}
				Category category = categoryRepository.findById(discountHotSaleDto.getCategoryId())
						.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CATEGORY_NOT_FOUND));
			}
		}
		if(programDiscountHotSaleDto.getProgramType()==ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT){

		}
		// Kiểm tra các điều kiện khác nếu cần, ví dụ ngày hết hạn, tên, mã code v.v.
		if (programDiscountHotSaleDto.getEndDate() == null || programDiscountHotSaleDto.getEndDate().before(Timestamp.valueOf(LocalDateTime.now()))) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_END_DATE);
		}

		ProgramDiscount programDiscount=new ProgramDiscount();

		programDiscount.setName(programDiscountHotSaleDto.getName());
		programDiscount.setProgramDiscountType(programDiscountHotSaleDto.getProgramDiscountType());
		programDiscount.setDeleted(false);
		programDiscount.setCreatedAt(LocalDateTime.now());
		programDiscount.setStartDate(programDiscountHotSaleDto.getStartDate().toLocalDateTime());
		programDiscount.setEndDate(programDiscountHotSaleDto.getEndDate().toLocalDateTime());
		if (programDiscountHotSaleDto.getProgramType() == ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORIES) {
			programDiscount.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORIES);
			for(DiscountHotSaleDto discountHotSaleDto:programDiscountHotSaleDto.getListDiscountHotSaleDto()){
				{
					Discount discount = new Discount();
					discount.setDeleted(false);
					discount.setDiscountType(discountHotSaleDto.getDiscountType());

					if(discountHotSaleDto.getDiscountType()==DiscountType.PERCENTAGE){
						discount.setDiscountPercentage(discountHotSaleDto.getDiscountPercentage());
						discount.setMaxDiscountAmount(discountHotSaleDto.getMaximumAmount());
						discount.setMinOrderAmount(discountHotSaleDto.getMinimumAmount());
					}
					else {
						discount.setDiscountAmount(discountHotSaleDto.getDiscountAmount());
					}
					List<Category> listCategory=categoryRepository.findAll();
					if(listCategory.isEmpty()){

					}
					for(Category category:listCategory){
						discount.setListCategory(new ArrayList<>());
						discount.getListCategory().add(category);
					}

					programDiscount.addDiscount(discount);
				}
			}
		}
		else if(programDiscountHotSaleDto.getProgramType()==ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORY){
			programDiscount.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORY);

			for(DiscountHotSaleDto discountHotSaleDto:programDiscountHotSaleDto.getListDiscountHotSaleDto()){
				{
					Category category=categoryRepository.findById(discountHotSaleDto.getCategoryId()).get();
					Discount discount = new Discount();
					discount.setDeleted(false);
					discount.setDiscountType(discountHotSaleDto.getDiscountType());
					discount.setListCategory(new ArrayList<>());

					if(discountHotSaleDto.getDiscountType()==DiscountType.PERCENTAGE){
						discount.setDiscountPercentage(discountHotSaleDto.getDiscountPercentage());
						discount.setMaxDiscountAmount(discountHotSaleDto.getMaximumAmount());
						discount.setMinOrderAmount(discountHotSaleDto.getMinimumAmount());
					}
					else {
						discount.setDiscountAmount(discountHotSaleDto.getDiscountAmount());
					}

					discount.getListCategory().add(category);
					programDiscount.addDiscount(discount);
				}
			}
		}
		else if(programDiscountHotSaleDto.getProgramType()==ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT){
			programDiscount.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT);
			for(DiscountHotSaleDto discountHotSaleDto:programDiscountHotSaleDto.getListDiscountHotSaleDto()){
						Optional<ProductVariant> productVariant=productVariantRepository.findConflictingProductVariants(
								discountHotSaleDto.getProductVariantId(),
								ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT,
								programDiscountHotSaleDto.getStartDate().toLocalDateTime(),
								programDiscountHotSaleDto.getEndDate().toLocalDateTime());
//						List<Discount> listDiscountByVariant=productVariant.getListRelDiscountProduct().stream().map(RelDiscountProduct::getDiscount).toList();
//						for(Discount discount:listDiscountByVariant){
//							if(discount.getProgramDiscount().getProgramType().equals(ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT) && discount.getProgramDiscount().getStartDate().isAfter(programDiscountHotSaleDto.getStartDate().toLocalDateTime())&&discount.getProgramDiscount().getStartDate().isBefore(programDiscountHotSaleDto.getEndDate().toLocalDateTime())){
//								throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISCOUNT_HOTSALE_EXIST_IN_VARIANT_IN_TIME+" "+productVariant.getId());
//							}
//						}
						if(productVariant.isPresent()){
							throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISCOUNT_HOTSALE_EXIST_IN_VARIANT_IN_TIME+" "+productVariant.get().getId());
						}
						ProductVariant productVariant1=productVariantRepository.findById(discountHotSaleDto.getProductVariantId()).get();
						Product product=productRepository.findById(discountHotSaleDto.getProductId()).get();


						if(product==null){
							throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND);
						}
						Discount discount = new Discount();
						discount.setDeleted(false);
						discount.setDiscountType(discountHotSaleDto.getDiscountType());
						discount.setPurchaseLimit(discountHotSaleDto.getPurchaseLimit());
						discount.setQuantityLimit(discountHotSaleDto.getQuantityLimit());

						if(discountHotSaleDto.getDiscountType()==DiscountType.PERCENTAGE){
								discount.setDiscountPercentage(discountHotSaleDto.getDiscountPercentage());
							}
						else{
								discount.setDiscountAmount(discountHotSaleDto.getDiscountAmount());
							}

						RelDiscountProduct relDiscountProduct=new RelDiscountProduct();
						relDiscountProduct.setProduct(product);
						relDiscountProduct.setProductVariant(productVariant1);
						relDiscountProduct.setDeleted(false);
						discount.addRelDiscountProduct(relDiscountProduct);

						programDiscount.addDiscount(discount);
						}
			}


		programDiscountRepository.save(programDiscount);
		return programDiscount;
	}

	@Override
	@Transactional
	public ProgramDiscountResponse createDiscountComboGift(ProgramDiscountGiftDto programDiscountComboDto){

		ProgramDiscount programDiscountCheckName=programDiscountRepository.findByName(programDiscountComboDto.getName());
		if(programDiscountCheckName!=null){throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PROGRAM_DISCOUNT_NAME_ALREADY_EXISTS);}

		if(programDiscountComboDto.getStartDate().toLocalDateTime().isAfter(programDiscountComboDto.getEndDate().toLocalDateTime())){throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.START_DATE_AFTER_END_DATE);}
		if(programDiscountComboDto.getProgramDiscountType()!=ProgramDiscountType.COMBO){throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_PROGRAM_TYPE);}

		ProgramDiscount programDiscount=new ProgramDiscount();
//		programDiscount.setProgramType(discountComboDto.getProgramType());


		DiscountGiftDto discountComboDto=programDiscountComboDto.getDiscountComboDto();
			if(discountComboDto.getProgramType()!=ProgramType.DISCOUNT_COMBO_GIFT_PRODUCT){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_PROGRAM_TYPE);
			}
			List<Product> listProductCheck=productRepository.findA(discountComboDto.getListProductMainId(),programDiscountComboDto.getStartDate().toLocalDateTime(),programDiscountComboDto.getEndDate().toLocalDateTime());
			if(!listProductCheck.isEmpty()){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DISCOUNT_COMBO_GIFT_PRODUCT_MAIN_EXIST_IN_TIME);
			}
			List<Product> listProductSide=productRepository.findAllById(discountComboDto.getListProductId());
			if(listProductSide.size()!=discountComboDto.getListProductId().size()){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_SIDE_NOT_FOUND);
			}
			if(discountComboDto.getListProductId().contains(discountComboDto.getListProductId())){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_PRODUCT_MAIN_ID_GIFT);
			}



			List<Product> listProductMain=productRepository.findAllById(discountComboDto.getListProductMainId());
			if(listProductMain.size()!=discountComboDto.getListProductMainId().size()){
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_MAIN_NOT_FOUND);
			}
			for(Long productMainId:discountComboDto.getListProductMainId()){
				Discount discount=new Discount();
				discount.setDeleted(false);
				discount.setDiscountType(DiscountType.GIFT);


				Product product=listProductMain.stream().filter(product1->product1.getId().equals(productMainId)).findFirst().get();
				RelDiscountProduct relDiscountProductMain=new RelDiscountProduct();
				relDiscountProductMain.setProduct(product);
				relDiscountProductMain.setIsMainProduct(true);
				relDiscountProductMain.setDeleted(false);
				discount.addRelDiscountProduct(relDiscountProductMain);

				for(Product productSide:listProductSide){
					RelDiscountProduct relDiscountProduct=new RelDiscountProduct();
					relDiscountProduct.setProduct(productSide);
					relDiscountProduct.setIsMainProduct(false);
					relDiscountProduct.setDeleted(false);
					discount.addRelDiscountProduct(relDiscountProduct);
				}

				programDiscount.addDiscount(discount);

			}

		programDiscount.setName(programDiscountComboDto.getName());
		programDiscount.setDeleted(false);
		programDiscount.setProgramDiscountType(ProgramDiscountType.COMBO);
		programDiscount.setCreatedAt(LocalDateTime.now());

		programDiscount.setStartDate(programDiscountComboDto.getStartDate().toLocalDateTime());
		programDiscount.setEndDate(programDiscountComboDto.getEndDate().toLocalDateTime());

		 programDiscountRepository.save(programDiscount);
		return convertProgramDiscountResponse(programDiscount);
	}



	public ProgramDiscountResponse convertProgramDiscountResponse(ProgramDiscount programDiscount){
		ProgramDiscountResponse programDiscountResponse=new ProgramDiscountResponse();
		programDiscountResponse.setId(programDiscount.getId());
		programDiscountResponse.setName(programDiscount.getName());
		programDiscountResponse.setStartDate(programDiscount.getStartDate());
		programDiscountResponse.setEndDate(programDiscount.getEndDate());
		programDiscountResponse.setProgramDiscountType(programDiscount.getProgramDiscountType());

		List<DiscountComboResponse> discountComboResponseList=new ArrayList<>();
		for(Discount discount:programDiscount.getListDiscount()){
			DiscountComboResponse discountComboResponse=new DiscountComboResponse();
			discountComboResponse.setDiscountType(discount.getDiscountType());
			discountComboResponse.setId(discount.getId());


			Product product=discount.getListRelDiscountProduct().stream().filter(rel-> rel.getIsMainProduct()!=null&&rel.getIsMainProduct()).map(r->r.getProduct()).findFirst().get();
			ProductResponeSimpleResponse productResponeSimpleResponse=modelMapper.map(product,ProductResponeSimpleResponse.class);

			List<Product> listProduct=discount.getListRelDiscountProduct().stream().filter(rel->rel.getIsMainProduct()!=null&&!rel.getIsMainProduct()).map(r->r.getProduct()).toList();
			List<ProductResponeSimpleResponse> productResponeSimpleResponseList=new ArrayList<>();
			for(Product product1:listProduct){
				ProductResponeSimpleResponse productResponeSimpleResponse1=modelMapper.map(product1,ProductResponeSimpleResponse.class);
				productResponeSimpleResponseList.add(productResponeSimpleResponse1);
			}

			discountComboResponse.setProductMainResponse(productResponeSimpleResponse);
			discountComboResponse.setListProductSideResponse(productResponeSimpleResponseList);

			discountComboResponseList.add(discountComboResponse);

		}
		programDiscountResponse.setDiscountComboResponseList(discountComboResponseList);
		return programDiscountResponse;
	}


	@Override
	public ProgramDiscountResponse getProgramDiscountCombo(Long id){
		ProgramDiscount programDiscount=programDiscountRepository.findById(id).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.NOT_FOUND_PROGRAM_DISCOUNT_COMBO));
		return convertProgramDiscountResponse(programDiscount);
	}

	@Override
	public ProgramDiscountHotSaleResponse getProgramDiscountHotSale(Long id) {
		ProgramDiscount programDiscount=programDiscountRepository.findById(id).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.NOT_FOUND_PROGRAM_DISCOUNT_HOT_SALE));

		ProgramDiscountHotSaleResponse programDiscountHotSaleResponse=new ProgramDiscountHotSaleResponse();

		programDiscountHotSaleResponse.setId(programDiscount.getId());
		programDiscountHotSaleResponse.setName(programDiscount.getName());
		programDiscountHotSaleResponse.setStartDate(Timestamp.valueOf(programDiscount.getStartDate()));
		programDiscountHotSaleResponse.setEndDate(Timestamp.valueOf(programDiscount.getEndDate()));
		programDiscountHotSaleResponse.setProgramDiscountType(programDiscount.getProgramDiscountType());
		List<DiscountHotSaleResponse> listDiscountHotSaleResponse=new ArrayList<>();


		if(programDiscount.getProgramType().equals(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORIES)){
			DiscountHotSaleResponse discountHotSaleResponse=new DiscountHotSaleResponse();
			Discount discount=programDiscount.getListDiscount().get(0);
			discountHotSaleResponse.setId(discount.getId());
			programDiscountHotSaleResponse.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORIES);

			if(discount.getDiscountType().equals(DiscountType.PERCENTAGE)){
				discountHotSaleResponse.setDiscountType(DiscountType.PERCENTAGE);
				discountHotSaleResponse.setDiscountPercentage(discount.getDiscountPercentage());
				discountHotSaleResponse.setDiscountAmount(discount.getDiscountAmount());
				discountHotSaleResponse.setMaximumAmount(discount.getMaxDiscountAmount());
				discountHotSaleResponse.setMinimumAmount(discount.getMinOrderAmount());
			}
			else if(discount.getDiscountType().equals(DiscountType.VALUE)){
				discountHotSaleResponse.setDiscountType(DiscountType.VALUE);
				discountHotSaleResponse.setDiscountAmount(discount.getDiscountAmount());
			}
			listDiscountHotSaleResponse.add(discountHotSaleResponse);
			programDiscountHotSaleResponse.setListDiscountHotSaleResponse(listDiscountHotSaleResponse);
		}
		else if(programDiscount.getProgramType().equals(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORY)){
			DiscountHotSaleResponse discountHotSaleResponse=new DiscountHotSaleResponse();
			Discount discount=programDiscount.getListDiscount().get(0);
			discountHotSaleResponse.setId(discount.getId());
			programDiscountHotSaleResponse.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_CATEGORY);

			if(discount.getDiscountType().equals(DiscountType.PERCENTAGE)){
				discountHotSaleResponse.setDiscountType(DiscountType.PERCENTAGE);
				discountHotSaleResponse.setDiscountPercentage(discount.getDiscountPercentage());
				discountHotSaleResponse.setDiscountAmount(discount.getDiscountAmount());
				discountHotSaleResponse.setMaximumAmount(discount.getMaxDiscountAmount());
				discountHotSaleResponse.setMinimumAmount(discount.getMinOrderAmount());
			}
			else if(discount.getDiscountType().equals(DiscountType.VALUE)){
				discountHotSaleResponse.setDiscountType(DiscountType.VALUE);
				discountHotSaleResponse.setDiscountAmount(discount.getDiscountAmount());
			}
			Category category=categoryRepository.findById(discount.getListCategory().get(0).getId()).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.NOT_FOUND_CATEGORY));

			discountHotSaleResponse.setCategoryId(category.getId());
			discountHotSaleResponse.setCategoryName(category.getName());

			listDiscountHotSaleResponse.add(discountHotSaleResponse);
			programDiscountHotSaleResponse.setListDiscountHotSaleResponse(listDiscountHotSaleResponse);
		}
		else if(programDiscount.getProgramType().equals(ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT)){
			programDiscountHotSaleResponse.setProgramType(ProgramType.DISCOUNT_HOTSALE_FOR_LIST_PRODUCT);

			for(Discount discount1:programDiscount.getListDiscount()){
				DiscountHotSaleResponse discountHotSaleResponse=new DiscountHotSaleResponse();
				discountHotSaleResponse.setId(discount1.getId());
				discountHotSaleResponse.setProductId(discount1.getListRelDiscountProduct().get(0).getProductVariant().getProduct().getId());

				for(RelDiscountProduct relDiscountProduct:discount1.getListRelDiscountProduct()){
					ProductVariant productVariant=relDiscountProduct.getProductVariant();
					discountHotSaleResponse.setProductId(productVariant.getProduct().getId());
					discountHotSaleResponse.setProductImage(productVariant.getProduct().getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
					discountHotSaleResponse.setProductVariantId(productVariant.getId());
					discountHotSaleResponse.setQuantityLimit(discount1.getQuantityLimit());
					discountHotSaleResponse.setPurchaseLimit(discount1.getPurchaseLimit());
					discountHotSaleResponse.setQuantity(productVariant.getQuantity());

					if(discount1.getDiscountType().equals(DiscountType.PERCENTAGE)){
						discountHotSaleResponse.setDiscountPercentage(discount1.getDiscountPercentage());
						discountHotSaleResponse.setDiscountAmount(discount1.getDiscountAmount());
						discountHotSaleResponse.setMaximumAmount(discount1.getMaxDiscountAmount());
						discountHotSaleResponse.setMinimumAmount(discount1.getMinOrderAmount());
					}
					else{
						discountHotSaleResponse.setDiscountAmount(discount1.getDiscountAmount());
						discountHotSaleResponse.setDiscountType(DiscountType.VALUE);
					}
				}
				listDiscountHotSaleResponse.add(discountHotSaleResponse);

			}
			programDiscountHotSaleResponse.setListDiscountHotSaleResponse(listDiscountHotSaleResponse);
		}

		return programDiscountHotSaleResponse;
	}

	@Override
	@Transactional
	public ProgramDiscountCombo1Response createDisCountCombo(ProgramDiscountComboDto programDiscountComboDto) {
		ProgramDiscount programDiscountCheckName=programDiscountRepository.findByName(programDiscountComboDto.getName());
		if(programDiscountCheckName!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PROGRAM_DISCOUNT_EXIST_NAME);
		}
		List<Product> listProductCheck=productRepository.findB(programDiscountComboDto.getListProductMainId(),programDiscountComboDto.getStartDate().toLocalDateTime(),programDiscountComboDto.getEndDate().toLocalDateTime());

		if(listProductCheck.size()>0){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_HAS_DISCOUNT_COMBO+""+listProductCheck.stream().map(Product::getId).toList());
		}

		List<Product> listProductSide=productRepository.findAllById(programDiscountComboDto.getListDiscountCombo().stream().map(DiscountComboDto::getProductId).toList());
		if(listProductSide.size()!=programDiscountComboDto.getListDiscountCombo().size()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND);
		}

		ProgramDiscount programDiscount=new ProgramDiscount();
		programDiscount.setName(programDiscountComboDto.getName());
		programDiscount.setProgramType(programDiscountComboDto.getProgramType());
		programDiscount.setStartDate(programDiscountComboDto.getStartDate().toLocalDateTime());
		programDiscount.setEndDate(programDiscountComboDto.getEndDate().toLocalDateTime());
		programDiscount.setListDiscount(new ArrayList<>());

		for(Long productId:programDiscountComboDto.getListProductMainId()){
			Product product=productRepository.findById(productId).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.NOT_FOUND_PRODUCT_SIDE));
			for(DiscountComboDto discountComboDto:programDiscountComboDto.getListDiscountCombo()){
				Discount discount=new Discount();
				discount.setDiscountType(discountComboDto.getDiscountType());
				discount.setQuantityLimit(discountComboDto.getQuantityLimit());
				discount.setPurchaseLimit(discountComboDto.getPurchaseLimit());
				discount.setMinOrderAmount(discountComboDto.getMinimumAmount());
				discount.setMaxDiscountAmount(discountComboDto.getMaximumAmount());
				if(discountComboDto.getDiscountType().equals(DiscountType.PERCENTAGE)){
					discount.setDiscountPercentage(discountComboDto.getDiscountPercentage());

				}
				else{
					discount.setDiscountAmount(discountComboDto.getDiscountAmount());
				}
				discount.setListRelDiscountProduct(new ArrayList<>());
				discount.setDeleted(false);

				RelDiscountProduct relDiscountProduct=new RelDiscountProduct();
				relDiscountProduct.setDiscount(discount);
				relDiscountProduct.setCreatedAt(LocalDateTime.now());
				relDiscountProduct.setDeleted(false);
				relDiscountProduct.setProduct(product);
				relDiscountProduct.setIsMainProduct(true);

				Product productSide=productRepository.findById(discountComboDto.getProductId()).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.NOT_FOUND_PRODUCT_SIDE));

				RelDiscountProduct relDiscountProductSide=new RelDiscountProduct();
				relDiscountProductSide.setDiscount(discount);
				relDiscountProductSide.setCreatedAt(LocalDateTime.now());
				relDiscountProductSide.setDeleted(false);
				relDiscountProductSide.setProduct(productSide);
				relDiscountProductSide.setIsMainProduct(false);

				discount.getListRelDiscountProduct().add(relDiscountProduct);
				discount.getListRelDiscountProduct().add(relDiscountProductSide);
//				discount.setProgramDiscount(programDiscount);
				programDiscount.addDiscount(discount);
			}
		}
		programDiscountRepository.save(programDiscount);
		return convert(programDiscount);
	}

	@Override
	public ProgramDiscountCombo1Response getProgramDiscountCombo1(Long id) {
		ProgramDiscount programDiscount=programDiscountRepository.findById(id).orElseThrow(()-> new ExceptionOm(HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()),ErrorMessageOm.PROGRAM_DISCOUNT_NOT_FOUND));
		return convert(programDiscount);
	}


	public ProgramDiscountCombo1Response convert(ProgramDiscount programDiscount) {
		ProgramDiscountCombo1Response programDiscountCombo1Response=new ProgramDiscountCombo1Response();
		programDiscountCombo1Response.setId(programDiscount.getId());
		programDiscountCombo1Response.setName(programDiscount.getName());
		programDiscountCombo1Response.setProgramType(programDiscount.getProgramType());
		programDiscountCombo1Response.setStartDate(Timestamp.valueOf(programDiscount.getStartDate()));
		programDiscountCombo1Response.setEndDate(Timestamp.valueOf(programDiscount.getEndDate()));
		programDiscountCombo1Response.setProgramDiscountType(programDiscount.getProgramDiscountType());

		programDiscountCombo1Response.setListProductMainResponse(new ArrayList<>());
		programDiscountCombo1Response.setListDiscountCombo1Response(new ArrayList<>());
		for(Discount discount:programDiscount.getListDiscount()){
			List<RelDiscountProduct> listRel=discount.getListRelDiscountProduct().stream().filter(relDiscountProduct -> relDiscountProduct.getIsMainProduct()==true).toList();
			List<Product> listProduct=listRel.stream().map(relDiscountProduct -> relDiscountProduct.getProduct()).toList();
			for(Product product:listProduct){
				ProductResponeSimpleResponse productResponeSimpleResponse=new ProductResponeSimpleResponse();
				productResponeSimpleResponse.setId(product.getId());
				productResponeSimpleResponse.setName(product.getName());
				productResponeSimpleResponse.setUrl(product.getImages().stream().filter(image -> image.getIsCover()==true).findFirst().orElseThrow().getUrl());
				productResponeSimpleResponse.setMinPrice(product.getMinPrice());
				programDiscountCombo1Response.getListProductMainResponse().add(productResponeSimpleResponse);
			}

			DiscountCombo1Response discountCombo1Response=new DiscountCombo1Response();

			List<RelDiscountProduct> listRelSide=discount.getListRelDiscountProduct().stream().filter(relDiscountProduct -> relDiscountProduct.getIsMainProduct()==false).toList();
			List<Product> listProductSide=listRelSide.stream().map(relDiscountProduct -> relDiscountProduct.getProduct()).toList();

			discountCombo1Response.setProductId(listProductSide.get(0).getId());
			discountCombo1Response.setProductImage(listProductSide.get(0).getImages().stream().filter(image -> image.getIsCover()==true).findFirst().orElseThrow().getUrl());
			discountCombo1Response.setPurchaseLimit(discount.getPurchaseLimit());
			discountCombo1Response.setQuantityLimit(discount.getQuantityLimit());

			if(discount.getDiscountType()==DiscountType.PERCENTAGE){
				discountCombo1Response.setDiscountPercentage(discount.getDiscountAmount());
			}
			else{
				discountCombo1Response.setDiscountAmount(discount.getDiscountAmount());
			}
			programDiscountCombo1Response.getListDiscountCombo1Response().add(discountCombo1Response);
		}


		return programDiscountCombo1Response;
	}

}

//
//			else if(discountHotSaleDto.getProgramType()==ProgramType.DISCOUNT_HOTSALE_FOR_PRODUCTS){
//
//		if(discountHotSaleDto.getListDiscountHotSaleItemDto()==null||discountHotSaleDto.getListDiscountHotSaleItemDto().isEmpty()){
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_LIST_DISCOUNT_HOTSALE_ITEM_DTO);
//				}
//						for(DiscountHotSaleItemDto discountHotSaleItemDto:discountHotSaleDto.getListDiscountHotSaleItemDto()){
//List<Long> listProductVariantId= discountHotSaleItemDto.getListDiscountHotSaleItemVariantDto().stream().map(DiscountHotSaleItemVariantDto::getProductVariantId).toList();
//Product product=productRepository.findByIdAndVariant(discountHotSaleItemDto.getProductId(),listProductVariantId);
//					if(product==null){
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_NOT_FOUND);
//					}
//List<ProductVariant> listProductVariant=productVariantRepository.findAllById(listProductVariantId);
//					if(listProductVariant.size()!=listProductVariantId.size()){
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PRODUCT_VARIANT_NOT_FOUND);
//					}
//							}
//							}
