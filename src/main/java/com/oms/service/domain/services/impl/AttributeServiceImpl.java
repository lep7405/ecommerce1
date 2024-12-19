package com.oms.service.domain.services.impl;
import com.ommanisoft.common.exceptions.ExceptionOm;

import com.oms.service.app.dtos.Category.AttributeDto;
import com.oms.service.app.response.AttributeResponse;
import com.oms.service.app.response.AttributeValueResponse;
import com.oms.service.app.response.ResponsePage;
import com.oms.service.domain.Utils.AttributeUtils;
import com.oms.service.domain.entities.AttValue;
import com.oms.service.domain.entities.Product.Attribute;

import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.exceptions.ErrorMessageOm;

import com.oms.service.domain.repositories.AttributeRepository;
import com.oms.service.domain.services.AttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeServiceImpl implements AttributeService {

	private final AttributeRepository attributeRepository;
	private final ModelMapper modelMapper;


	@Override
	@Transactional
	public AttributeResponse createAttribute(AttributeDto attributeDto) {
		if(attributeDto.getListAttributeValues()==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE);
		}
		Attribute attribute = AttributeUtils.createAttribute(attributeDto,false);
		attributeRepository.save(attribute);
		return modelMapper.map(attribute, AttributeResponse.class);
	}

	@Override
	@Transactional
	public AttributeResponse updateAttribute(Long id, AttributeDto attributeDto) {
		Attribute attribute = attributeRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ATTRIBUTE_NOT_FOUND));
		AttributeUtils.updateAttribute(attribute, attributeDto);
		attributeRepository.save(attribute);
		return modelMapper.map(attribute, AttributeResponse.class);
	}

	@Override
	public ResponsePage<Attribute, AttributeResponse> getAllAttributes(Pageable pageable) {
		Pageable sortedByAlphabet = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending() // Sắp xếp theo trường "name" theo thứ tự bảng chữ cái
		);
		Page<Attribute> attributes = attributeRepository.findAll(sortedByAlphabet);
		return new ResponsePage<>(attributes, AttributeResponse.class);
	}

	@Override
	@Transactional
	public AttributeResponse deleteAttribute(Long id) {
		Attribute attribute = attributeRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ATTRIBUTE_NOT_FOUND));
		attribute.setDeleted(true);
		attributeRepository.save(attribute);
		return AttributeResponse.builder().id(id).build();
	}

	@Override
	public AttributeResponse createAtributeBrand(AttributeDto attributeDto){
		Attribute attribute=attributeRepository.findByNameIgnoreCase("brand").orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ATTRIBUTE_NOT_FOUND));
		attributeDto.getListAttributeValues().forEach(value -> {
			AttributeValue attributeValue =new AttributeValue();
			AttValue attValue = new AttValue();
			attValue.setAttValueString(value.toString());
			attributeValue.setAttValue(attValue);
			attributeValue.setCreatedAt(LocalDateTime.now());
			attributeValue.setDeleted(false);
			attribute.addAttributeValue(attributeValue);
		});

		attributeRepository.save(attribute);
		return modelMapper.map(attribute, AttributeResponse.class);
	}
	@Override
	public AttributeResponse getAllAttributesBrand(){
		Attribute attribute=attributeRepository.findByNameIgnoreCase("brand").orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ATTRIBUTE_NOT_FOUND));
		AttributeResponse attributeResponse=new AttributeResponse();
		attributeResponse.setId(attribute.getId());
		attributeResponse.setName(attribute.getName());
		for(AttributeValue attributeValue:attribute.getListAttributeValue()){
			AttributeValueResponse attributeValueResponse=new AttributeValueResponse();
			attributeValueResponse.setId(attributeValue.getId());
			attributeValueResponse.setAttValueString(attributeValue.getAttValue().getAttValueString());

			attributeResponse.getListAttributeValue().add(attributeValueResponse);
		}
		return attributeResponse;
	}
}
