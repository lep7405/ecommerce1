package com.oms.service.domain.services;

import com.oms.service.app.dtos.Category.AttributeDto;
import com.oms.service.app.response.AttributeResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.entities.Product.Attribute;
import org.springframework.data.domain.Pageable;

public interface AttributeService {
  AttributeResponse createAttribute(AttributeDto attributeDto);
  AttributeResponse updateAttribute(Long id, AttributeDto attributeDto);
//  MessageResponse addListAttributeValueToAttribute(Long id, List<AttributeValueDto> listAttributeValueDto);
//  MessageResponse deleteListAttributeValueFromAttribute(Long id, List<Long> listAttributeValueIds);

  ResponsePage<Attribute, AttributeResponse> getAllAttributes(Pageable pageable);
  AttributeResponse deleteAttribute(Long id);

  AttributeResponse createAtributeBrand(AttributeDto attributeDto);

  AttributeResponse getAllAttributesBrand();
}
