package com.oms.service.domain.services;

import com.oms.service.app.dtos.FilterDto;
import com.oms.service.app.dtos.Product.ProductDto;
import com.oms.service.app.response.ProductResponse;
import com.oms.service.app.response.product.ProductResponeSimpleResponse;
import com.oms.service.domain.entities.Product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ProductService {

  ProductResponse createProduct(ProductDto productDto);
  ProductResponse getProductDetail(Long id);

  ProductResponse updateProduct(Long id, ProductDto productDto);


  Page<Product> getAllProducts(FilterDto filterDto,Pageable pageable);

  ProductResponse deleteProduct(Long id);

  void deleteProducts();

  List<ProductResponeSimpleResponse> getAllProducts(Long categoryId);
}

