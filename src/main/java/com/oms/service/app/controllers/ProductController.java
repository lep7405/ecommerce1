package com.oms.service.app.controllers;

import com.oms.service.app.dtos.FilterDto;
import com.oms.service.app.dtos.Product.ProductDto;
import com.oms.service.app.response.*;
import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.domain.Utils.S3Service;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.repositories.ProductRepository;
import com.oms.service.domain.services.ProductService;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {

  private final ProductService productService;
  private final ProductRepository productRepository;
  private final S3Service service;
  private final ModelMapper modelMapper;

  @PostMapping
  public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductDto productDto) {
    return new ApiResponse<>(HttpStatus.OK.value(),productService.createProduct(productDto));
  }
  //test
  @GetMapping("/{id}")
  @Transactional
  public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
    return new ApiResponse<>(HttpStatus.OK.value(),productService.getProductDetail(id));
  }

  @PutMapping("/{id}")
  public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
    return new ApiResponse<>(HttpStatus.OK.value(),productService.updateProduct(id,productDto));
  }

  @GetMapping("/all")
  public Page<Product> getAllProducts(@ModelAttribute FilterDto filterDto, Pageable pageable) {
    return productService.getAllProducts(filterDto,pageable);
  }

  @DeleteMapping("/{id}")
  public ApiResponse<ProductResponse> deleteProduct(@PathVariable Long id) {
    return new ApiResponse<>(HttpStatus.OK.value(),productService.deleteProduct(id));
  }

  @DeleteMapping("/_bulk")
  public ApiResponse<?> deleteProducts() {
     productService.deleteProducts();
     return new ApiResponse<>(HttpStatus.NO_CONTENT.value());
  }

  @PostMapping("/image")
  public String createImage(@RequestParam("files") MultipartFile files) throws IOException {
    service.uploadToS3(files, "review");
    return service.getPresignedUrl("review"+files.getOriginalFilename());
  }




  @GetMapping("/category/{id}")
  public ApiResponse<List<ProductResponeSimpleResponse>> getAllProductsByCategory(@PathVariable("id") Long id) {
    return new ApiResponse<>(HttpStatus.OK.value(),productService.getAllProducts(id));
  }

  @GetMapping("/getAllProducts")
  public ApiResponse<List<ProductResponeSimpleResponse>> getAllProducts() {
    List<Product> listProduct=productRepository.findAll();
    List<ProductResponeSimpleResponse> listProductResponeSimpleResponse=new ArrayList<>();
    for(Product product:listProduct){
      ProductResponeSimpleResponse productResponeSimpleResponse=new ProductResponeSimpleResponse();
      productResponeSimpleResponse.setId(product.getId());
      productResponeSimpleResponse.setName(product.getName());
      productResponeSimpleResponse.setUrl(product.getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
      productResponeSimpleResponse.setMaxPrice(product.getMaxPrice());
      productResponeSimpleResponse.setMinPrice(product.getMinPrice());
      List<ProductVariantResponse> productVariantResponseList=new ArrayList<>();
      for(ProductVariant productVariant:product.getListProductVariants()){
        ProductVariantResponse productVariantResponse=new ProductVariantResponse();
        productVariantResponse.setId(productVariant.getId());
        productVariantResponse.setPrice(productVariant.getPrice());
        productVariantResponse.setQuantity(productVariant.getQuantity());

        List<AttributeValue> attributeValueList=productVariant.getListRelVariantValueProduct().stream().map(variantValue->variantValue.getAttributeValue()).toList();

        List<AttributeValueResponse> attributeResponseList=new ArrayList<>();
        for(AttributeValue attributeValue:attributeValueList){
          AttributeValueResponse attributeValueResponse=new AttributeValueResponse();
          attributeValueResponse.setAttValueString(attributeValue.getAttValue().getAttValueString());
          attributeResponseList.add(attributeValueResponse);
        }
        productVariantResponse.setAttributeValueResponses(attributeResponseList);

        productVariantResponseList.add(productVariantResponse);
      }
      productResponeSimpleResponse.setListProductVariants(productVariantResponseList);
      listProductResponeSimpleResponse.add(productResponeSimpleResponse);
    }
    return new ApiResponse<>(HttpStatus.OK.value(),listProductResponeSimpleResponse);
  }


  @PostMapping("/getAllProductsForDiscount")
  public ApiResponse<List<ProductResponeSimpleResponse>> getAllProductsForDiscount(   @RequestBody List<Long> excludeIds) {
    List<Product> listProduct = (excludeIds == null || excludeIds.isEmpty())
            ? productRepository.findAll()
            : productRepository.findAllByIdNotIn(excludeIds);
    List<ProductResponeSimpleResponse> listProductResponeSimpleResponse=new ArrayList<>();
    for(Product product:listProduct){
      ProductResponeSimpleResponse productResponeSimpleResponse=new ProductResponeSimpleResponse();
      productResponeSimpleResponse.setId(product.getId());
      productResponeSimpleResponse.setName(product.getName());
      productResponeSimpleResponse.setUrl(product.getImages().stream().filter(img->img.getIsCover()).findFirst().get().getUrl());
      productResponeSimpleResponse.setMaxPrice(product.getMaxPrice());
      productResponeSimpleResponse.setMinPrice(product.getMinPrice());
      List<ProductVariantResponse> productVariantResponseList=new ArrayList<>();
      for(ProductVariant productVariant:product.getListProductVariants()){
        ProductVariantResponse productVariantResponse=new ProductVariantResponse();
        productVariantResponse.setId(productVariant.getId());
        productVariantResponse.setPrice(productVariant.getPrice());
        productVariantResponse.setQuantity(productVariant.getQuantity());

        List<AttributeValue> attributeValueList=productVariant.getListRelVariantValueProduct().stream().map(variantValue->variantValue.getAttributeValue()).toList();

        List<AttributeValueResponse> attributeResponseList=new ArrayList<>();
        for(AttributeValue attributeValue:attributeValueList){
          AttributeValueResponse attributeValueResponse=new AttributeValueResponse();
          attributeValueResponse.setAttValueString(attributeValue.getAttValue().getAttValueString());
          attributeResponseList.add(attributeValueResponse);
        }
        productVariantResponse.setAttributeValueResponses(attributeResponseList);

        productVariantResponseList.add(productVariantResponse);
      }
      productResponeSimpleResponse.setListProductVariants(productVariantResponseList);
      listProductResponeSimpleResponse.add(productResponeSimpleResponse);
    }
    return new ApiResponse<>(HttpStatus.OK.value(),listProductResponeSimpleResponse);
  }
}
