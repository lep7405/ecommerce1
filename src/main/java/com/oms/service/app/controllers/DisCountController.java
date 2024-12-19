package com.oms.service.app.controllers;

import com.oms.service.app.dtos.Discount.DiscountCombo.ProgramDiscountComboDto;
import com.oms.service.app.dtos.Discount.DiscountGift.ProgramDiscountGiftDto;
import com.oms.service.app.dtos.Discount.DiscountHotSaleIDto.ProgramDiscountHotSaleDto;
import com.oms.service.app.dtos.Discount.ProgramDiscountFilterDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.DisCount.DiscountResponse;
import com.oms.service.app.response.DisCount.ProgramDiscountResponse;
import com.oms.service.app.response.DiscountCombo1.ProgramDiscountCombo1Response;
import com.oms.service.app.response.DiscountHotSale.ProgramDiscountHotSaleResponse;
import com.oms.service.app.response.ProductResponse;
import com.oms.service.domain.entities.Discount.Discount;
import com.oms.service.domain.entities.Discount.ProgramDiscount;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.repositories.Discount.DiscountRepository;
import com.oms.service.domain.repositories.Discount.ProgramDiscountRepository;
import com.oms.service.domain.repositories.ProductRepository;
import com.oms.service.domain.services.DiscountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discounts")
@Slf4j
public class DisCountController {
	private final DiscountService discountService;
	private final DiscountRepository discountRepository;
	private final ProductRepository productRepository;
	private final ProgramDiscountRepository programDiscountRepository;

	@PostMapping
	public ProgramDiscount createDiscount(@RequestBody @Valid ProgramDiscountHotSaleDto programDiscountHotSaleDto) {
		return discountService.createDiscountHotSale(programDiscountHotSaleDto);
	}

	@GetMapping("/getAll")
	public ApiResponse<List<ProgramDiscountResponse>> getAllDiscount(@ModelAttribute("DiscountFilterDto") ProgramDiscountFilterDto programDiscountFilterDto, Pageable pageable) {
		Page<ProgramDiscount> listProgramDiscount=programDiscountRepository.findAll(programDiscountFilterDto.getProgramType(),programDiscountFilterDto.getName(),programDiscountFilterDto.getStartDate(),programDiscountFilterDto.getEndDate(),pageable);
		List<ProgramDiscountResponse> listProgramDiscountResponse=new ArrayList<>();
		int totalPages = listProgramDiscount.getTotalPages();
		log.info("hello"+totalPages);
		for(ProgramDiscount programDiscount:listProgramDiscount.getContent()) {
			ProgramDiscountResponse programDiscountResponse=new ProgramDiscountResponse();
			programDiscountResponse.setId(programDiscount.getId());
			programDiscountResponse.setName(programDiscount.getName());
			programDiscountResponse.setStartDate(programDiscount.getStartDate());
			programDiscountResponse.setEndDate(programDiscount.getEndDate());
			programDiscountResponse.setProgramDiscountType(programDiscount.getProgramDiscountType());
			programDiscountResponse.setProgramType(programDiscount.getProgramType());

			List<DiscountResponse> listDiscountResponse=new ArrayList<>();
			for(Discount discount:programDiscount.getListDiscount()){
				DiscountResponse discountResponse=new DiscountResponse();
				discountResponse.setId(discount.getId());
				List<Product> listProduct=productRepository.findAllByDiscount(discount.getId());
				List<ProductResponse> listProductResponse=new ArrayList<>();
				if(listProduct.size()<=6){
					for(Product product:listProduct){
						ProductResponse productReponse=new ProductResponse();
						productReponse.setId(product.getId());
						productReponse.setName(product.getName());
						productReponse.setUrl(product.getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
						listProductResponse.add(productReponse);
					}

				}
				else{
					for(Product product:listProduct.stream().limit(6).toList()){
						ProductResponse productReponse=new ProductResponse();
						productReponse.setId(product.getId());
						productReponse.setName(product.getName());
						productReponse.setUrl(product.getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
						listProductResponse.add(productReponse);
					}
				}
				discountResponse.setListProductResponses(listProductResponse);
				listDiscountResponse.add(discountResponse);
			}
			programDiscountResponse.setListDiscountResponse(listDiscountResponse);
			listProgramDiscountResponse.add(programDiscountResponse);
		}
		return new ApiResponse<>(HttpStatus.OK.value(),listProgramDiscountResponse);
	}
	@PostMapping("/create")
	public ApiResponse<com.oms.service.app.response.DiscountCombo.ProgramDiscountResponse> createDiscountComboGift(@RequestBody @Valid ProgramDiscountGiftDto programDiscountComboDto) {
		return new ApiResponse<>(HttpStatus.OK.value(),discountService.createDiscountComboGift(programDiscountComboDto));
	}
	@PostMapping("/create/combo")
	public ApiResponse<ProgramDiscountCombo1Response> createDiscountCombo(@RequestBody @Valid ProgramDiscountComboDto programDiscountComboDto){
		return new ApiResponse<>(HttpStatus.OK.value(),discountService.createDisCountCombo(programDiscountComboDto));
	}
	@GetMapping("/combo/{id}")
	public ApiResponse<com.oms.service.app.response.DiscountCombo.ProgramDiscountResponse> getComboDiscount(@PathVariable("id") Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),discountService.getProgramDiscountCombo(id));
	}

	@GetMapping("/HotSale/{id}")
	public ApiResponse<ProgramDiscountHotSaleResponse> getHotSaleDiscount(@PathVariable("id") Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),discountService.getProgramDiscountHotSale(id));
	}

	@GetMapping("/combo1/{id}")
	public ApiResponse<ProgramDiscountCombo1Response> getCombo1Discount(@PathVariable("id") Long id){
		return new ApiResponse<>(HttpStatus.OK.value(),discountService.getProgramDiscountCombo1(id));
	}

}
