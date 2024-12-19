package com.oms.service.domain.Utils;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Category.AttributeDto;
import com.oms.service.app.dtos.Category.AttributeValueDto;
import com.oms.service.domain.entities.AttValue;
import com.oms.service.domain.entities.Product.Attribute;
import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.enums.DataType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AttributeUtils {

	public static Attribute createAttribute(AttributeDto attributeDto,Boolean isUpdate) {
		Attribute attribute = new Attribute();
		attribute.setName(attributeDto.getName());
		attribute.setIsRequired(attributeDto.getIsRequired());
		attribute.setIsSelect(attributeDto.getIsSelect());
		attribute.setIsSelectMultiple(attributeDto.getIsSelectMultiple());
		attribute.setDeleted(false);
		attribute.setDataType(attributeDto.getDataType());
		attribute.setIsForVariant(attributeDto.getIsForVariant());
		attribute.setCreatedAt(LocalDateTime.now());
		if(isUpdate){
			attributeDto.getListAttributeValueDto().forEach(value -> {
				AttributeValue attributeValue = createAttributeValue(value.getAttValue(), attributeDto.getDataType(), attributeDto.getIsSelect());
				attribute.addAttributeValue(attributeValue);
			});
		}
		else{
			attributeDto.getListAttributeValues().forEach(value -> {
				AttributeValue attributeValue = createAttributeValue(value, attributeDto.getDataType(), attributeDto.getIsSelect());
				attribute.addAttributeValue(attributeValue);
			});
		}


		return attribute;
	}
	private static AttributeValue createAttributeValue(Object value, DataType dataType, Boolean isSelect) {

		// Implement this method based on your requirements
		if (value == null && isSelect) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_VALUE_FORMAT.val());
		}
		AttValue attValue = new AttValue();
		AttributeValue attributeValue = new AttributeValue();
		if (value != null) {
			try {
				if (dataType == DataType.INTEGER) {
					attValue.setAttValueInt(Integer.parseInt((value.toString())));
				}
				else if (dataType == DataType.DOUBLE) {
					attValue.setAttValueDouble(Double.parseDouble(value.toString()));
				}
				else if(dataType == DataType.STRING) {
					String[] result = splitNumberAndText(value);
					if (result != null) {
						try {
							// Thử chuyển đổi thành Integer
							int intValue = Integer.parseInt(result[0]);
							attValue.setAttValueInt(intValue);
						} catch (NumberFormatException e1) {
							try {
								// Nếu không phải Integer, thử chuyển đổi thành Double
								double doubleValue = Double.parseDouble(result[0]);
								attValue.setAttValueDouble(doubleValue);
							} catch (NumberFormatException e2) {
								// Nếu không phải Double, không xử lý hoặc báo lỗi
								throw new IllegalArgumentException("Invalid number format: " + result[0]);
							}
						}
					}
					attValue.setAttValueString(value.toString());
				}
			} catch (NumberFormatException e) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_VALUE_FORMAT.val());
			}
			attributeValue.setAttValue(attValue);
		}
		attributeValue.setDeleted(false);
		attributeValue.setCreatedAt(LocalDateTime.now());

		return attributeValue;
	}

	public static void updateAttribute(Attribute attribute, AttributeDto attributeDto){
		if (attribute != null) {
			attribute.setName(attribute.getName());
			attribute.setIsSelect(attribute.getIsSelect());
			attribute.setIsRequired(attribute.getIsRequired());
			attribute.setUpdatedAt(LocalDateTime.now());
			log.info("attribute: {}", attribute.getName());

			if(attribute.getIsSelect()){
				List<AttributeValue> listAttributeValue = attribute.getListAttributeValue();
				List<AttributeValueDto> listAttributeValueDto= attributeDto.getListAttributeValueDto();

				List<Long> listIdAttributeValueOld = attribute.getListAttributeValue().stream().map(AttributeValue::getId).toList();
				List<Long> listIdAttributeValueDto = listAttributeValueDto.stream().map(AttributeValueDto::getId).toList();

				List<Long> listIdAttributeValueDelete = listIdAttributeValueOld.stream().filter(id -> !listIdAttributeValueDto.contains(id)).collect(Collectors.toList());
				deleteAttributeValue(listIdAttributeValueDelete, listAttributeValue);

				List<AttributeValueDto> listAttributeValueDtoNew=listAttributeValueDto.stream().filter(attv->attv.getId()==null).collect(Collectors.toList());
				addNewAttributeValue(attribute, listAttributeValueDtoNew);

				List<Long> listAttributeValueIdUpdate=listIdAttributeValueOld.stream().filter(listIdAttributeValueDto::contains).toList();
				List<AttributeValueDto> listAttributeValueDtoUpdate=listAttributeValueDto.stream().filter(attv->listAttributeValueIdUpdate.contains(attv.getId())).collect(Collectors.toList());
				updateAttributeValue(listAttributeValueDtoUpdate, attribute);
			}
		}
	}

	public static void deleteAttributeValue(List<Long> listIdAttributeValueDelete, List<AttributeValue> listAttributeValue) {
		listIdAttributeValueDelete.forEach(id -> listAttributeValue.stream()
				.filter(attr -> attr.getId().equals(id))
				.findFirst()
				.ifPresent(attr -> {attr.setDeleted(true); attr.setUpdatedAt(LocalDateTime.now());}));
	}
	public static void addNewAttributeValue(Attribute attributeDb, List<AttributeValueDto> listAttributeValueDtoNew) {
		listAttributeValueDtoNew.forEach(attributeValueDto-> {
			AttributeValue attributeValue = createAttributeValue(attributeValueDto.getAttValue(),attributeDb.getDataType(), attributeDb.getIsSelect());
			attributeDb.addAttributeValue(attributeValue);
		});
	}
	public static void updateAttributeValue(List<AttributeValueDto> listAttributeValueDtoUpdate, Attribute attribute) {
		listAttributeValueDtoUpdate.forEach(attributeValueDto-> {
			AttributeValue attributeValue = attribute.getListAttributeValue().stream()
					.filter(attr -> attr.getId().equals(attributeValueDto.getId()))
					.findFirst()
					.orElseThrow(() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessageOm.ATTRIBUTE_VALUE_NOT_FOUND));
			AttValue attValue = new AttValue();

			try {
				if (attribute.getDataType() == DataType.INTEGER) {
					attValue.setAttValueInt(Integer.parseInt((String) attributeValueDto.getAttValue()));

				}
				else if (attribute.getDataType() == DataType.DOUBLE) {
					attValue.setAttValueDouble(Double.parseDouble((String) attributeValueDto.getAttValue()));
				}
				else if(attribute.getDataType() == DataType.STRING) {
					String[] result = splitNumberAndText(attributeValueDto.getAttValue());
					if (result != null) {
						try {
							// Thử chuyển đổi thành Integer
							int intValue = Integer.parseInt(result[0]);
							attValue.setAttValueInt(intValue);
						} catch (NumberFormatException e1) {
							try {
								// Nếu không phải Integer, thử chuyển đổi thành Double
								double doubleValue = Double.parseDouble(result[0]);
								attValue.setAttValueDouble(doubleValue);
							} catch (NumberFormatException e2) {
								// Nếu không phải Double, không xử lý hoặc báo lỗi
								throw new IllegalArgumentException("Invalid number format: " + result[0]);
							}
						}
					}
					attValue.setAttValueString((String) attributeValueDto.getAttValue());
				}
			} catch (NumberFormatException e) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.INVALID_ATTRIBUTE_VALUE_FORMAT.val());
			}
//			boolean exists = attribute.getListAttributeValue().stream()
//					.anyMatch(attrValue -> attrValue.getAttValue().equals(attValue));
//
//			if (exists) {
//				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ATTRIBUTE_VALUE_ALREADY_EXISTS.val()+attributeValueDto.getAttValue());
//			}
			attributeValue.setCreatedAt(LocalDateTime.now());
			attributeValue.setAttValue(attValue);
		});
	}
	public static String[] splitNumberAndText(Object input) {
		// Biểu thức chính quy để tìm số và chữ
		String regex = "([0-9]+\\.?[0-9]*)\\s*([a-zA-Z]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((String) input);

		if (matcher.matches()) {
			return new String[]{matcher.group(1), matcher.group(2)};
		}
		return null;
	}

}

