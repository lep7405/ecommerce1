package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Filter.FilterItemDto;
import com.oms.service.app.dtos.Filter.FiltersDto;
import com.oms.service.app.response.AttributeResponse;
import com.oms.service.app.response.AttributeValueResponse;
import com.oms.service.app.response.Filters.FilterItemResponse;
import com.oms.service.app.response.Filters.FilterResponse;
import com.oms.service.domain.entities.Parameter;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.entities.Category;
import com.oms.service.domain.entities.Filter.FilterItem;
import com.oms.service.domain.entities.Filter.Filters;
import com.oms.service.domain.enums.FilterType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.AttributeRepository;
import com.oms.service.domain.repositories.AttributeValueRepository;
import com.oms.service.domain.repositories.CategoryRepository;
import com.oms.service.domain.repositories.Filter.FilterRepository;
import com.oms.service.domain.services.FilterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.oms.service.domain.Utils.Constant.PriceFilter;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {
	private final FilterRepository filterRepository;
	private final CategoryRepository categoryRepository;
	private final AttributeRepository attributeRepository;
	private final AttributeValueRepository attributeValueRepository;
	private final ModelMapper mapper;
	@Override
	@Transactional
	public List<FilterResponse> createListFitlters(Long categoryId,List<FiltersDto> listFiltersDtos) {
		if(listFiltersDtos.isEmpty()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.EMPTY_FILTER_LIST.val());
		}
		List<FiltersDto> listFilterDtoNoPrice=listFiltersDtos.stream().filter(filtersDto -> !Objects.equals(filtersDto.getName(), PriceFilter)).toList();
		FiltersDto filtersPriceDto=listFiltersDtos.stream().filter(filterDto -> Objects.equals(filterDto.getName(), PriceFilter)).findFirst().orElse(null);
		//check xem các filter name này có trùng với nhau không
		List<String> listFilterName=listFiltersDtos.stream().map(FiltersDto::getName).toList();
		if(listFilterName.size()!=listFilterName.stream().distinct().count()) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.DUPLICATE_FILTER_NAME.val());
		}

		Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_FOUND));

		//check attribute
		List<Long> listAttributeId=listFilterDtoNoPrice.stream().map(FiltersDto::getAttributeId).toList();
		Map<Long,Attribute> attributeMap=attributeRepository.findAllByCategory(listAttributeId,categoryId).stream().collect(Collectors.toMap(Attribute::getId, attr->attr));


		//check xem attribute value id gửi lên có đúng không
		List<Long> listAttributeValueId=listFilterDtoNoPrice.stream().flatMap(filtersDto->filtersDto.getListFiterItemDtos().stream().map(FilterItemDto::getAttributeValueId).filter(Objects::nonNull)).toList();
		List<AttributeValue> listAttributeValue=attributeValueRepository.findAllById(listAttributeValueId);

		//tìm kiếm attribute nhanh hơn
		Map<Long,AttributeValue> attributeValueMap=listAttributeValue.stream().collect(Collectors.toMap(AttributeValue::getId,attr->attr));
		//T
		List<Filters> listFilters= new ArrayList<>();
		Timestamp currentTime=Timestamp.valueOf(LocalDateTime.now());
		for(FiltersDto filtersDto:listFilterDtoNoPrice) {
			Filters filters=new Filters();
			Attribute attribute=attributeMap.get(filtersDto.getAttributeId());
			if(attribute==null) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE.val());
			}

			//nếu cái filter type là range thì attributeValueId phải là null
			//nếu cái filter type là list thì attributeValueId phải khác null và mấy cái min , max nó cũng cần phải là null
			for(FilterItemDto filterItemDto:filtersDto.getListFiterItemDtos()) {
				if(filtersDto.getFilterType().equals(FilterType.RANGE) &&filterItemDto.getAttributeValueId()!=null) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_FILTER_TYPE.val()+"1");
				}
				if(filtersDto.getFilterType().equals(FilterType.LIST) &&(filterItemDto.getAttributeValueId()==null||filterItemDto.getMin()!=null||filterItemDto.getMax()!=null)) {
					throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_FILTER_TYPE.val()+"2");
				}
				FilterItem filterItem=new FilterItem();
				if(filterItemDto.getAttributeValueId()!=null){
					AttributeValue attributeValue=attributeValueMap.get(filterItemDto.getAttributeValueId());
					filterItem.setAttributeValue(attributeValue);
				}
				filterItem.setCreatedAt(currentTime);
				filterItem.setDeleted(false);
				filterItem.setMins(filterItemDto.getMin());
				filterItem.setMaxs(filterItemDto.getMax());
				filterItem.setName(filterItemDto.getName());

				filters.addFilterItem(filterItem);
			}

			filters.setName(filtersDto.getName());
			filters.setCreatedAt(currentTime);
			filters.setDeleted(false);
			filters.setFilterType(filtersDto.getFilterType());
			filters.setFilterIndex(filtersDto.getFilterIndex());
			filters.setCategory(category);
			filters.setAttribute(attribute);

			listFilters.add(filters);
		}

		Filters filtersPrice=new Filters();
		filtersPrice.setName(PriceFilter);
		for(FilterItemDto filterItemDto:filtersPriceDto.getListFiterItemDtos()){
			FilterItem filterItem=new FilterItem();
			filterItem.setCreatedAt(currentTime);
			filterItem.setDeleted(false);
			filterItem.setMins(filterItemDto.getMin());
			filterItem.setMaxs(filterItemDto.getMax());
			filterItem.setName(filterItemDto.getName());

			filtersPrice.addFilterItem(filterItem);
		}
		listFilters.add(filtersPrice);

		filterRepository.saveAll(listFilters);

		return convertToFilterResponse(listFilters);
	}

	@Override
	@Transactional
	public List<FilterResponse> getFiltersByCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_FOUND));
		List<Filters> listFilters=category.getListFilter();
		return convertToFilterResponse(listFilters);
	}

	@Override
	public List<AttributeResponse> getAllAttributeByCategory(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(()->new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CATEGORY_NOT_FOUND));
		List<Attribute> listAttribute=category.getListParameter().stream().flatMap(param->param.getListAttributes().stream()).collect(Collectors.toList());
		List<Attribute> listAttributeCategory=category.getListAttribute();

		List<Attribute> combinedList = new ArrayList<>(listAttribute);
		combinedList.addAll(listAttributeCategory);

		List<AttributeResponse> listAttributeResponse=new ArrayList<>();
		for(Attribute attribute:combinedList) {
			AttributeResponse attributeResponse=new AttributeResponse();
			attributeResponse.setId(attribute.getId());
			attributeResponse.setName(attribute.getName());
			attributeResponse.setIsSelect(attribute.getIsSelect());

			List<AttributeValueResponse> listAttributeValueResponse=new ArrayList<>();
			for(AttributeValue attributeValue:attribute.getListAttributeValue()) {
				AttributeValueResponse attributeValueResponse=new AttributeValueResponse();
				attributeValueResponse.setId(attributeValue.getId());
				attributeValueResponse.setAttValueString(attributeValue.getAttValue().getAttValueString());
				listAttributeValueResponse.add(attributeValueResponse);

			}
			attributeResponse.setListAttributeValue(listAttributeValueResponse);

			listAttributeResponse.add(attributeResponse);
		}

		return listAttributeResponse;
	}

	public List<FilterResponse> convertToFilterResponse(List<Filters> listFilters) {
		mapper.typeMap(Filters.class, FilterResponse.class).addMappings(mapper -> {
			mapper.map(source->source.getAttribute().getId(), FilterResponse::setAttributeId);
			mapper.map(source ->source.getAttribute().getName(),FilterResponse::setAttributeName);
		});
		mapper.typeMap(FilterItem.class, FilterItemResponse.class).addMappings(mapper -> {
			mapper.map(source ->source.getAttributeValue().getId(),FilterItemResponse::setAttributeValueId);
		});
		List<FilterResponse> listFilterReponse=new ArrayList<>();
		for(Filters filters:listFilters) {
			FilterResponse filterResponse= mapper.map(filters,FilterResponse.class);
			listFilterReponse.add(filterResponse);
		}
		return listFilterReponse;
	}
}

