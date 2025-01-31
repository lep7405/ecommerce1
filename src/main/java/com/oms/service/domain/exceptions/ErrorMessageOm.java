package com.oms.service.domain.exceptions;

import com.ommanisoft.common.exceptions.BaseErrorMessage;

public enum ErrorMessageOm implements BaseErrorMessage {
  SUCCESS("Success"),
  FALSE("False"),
  CATEGORY_NOT_FOUND("Category not found"),
  NO_FILE_SELECTED("No file selected"),
  INVALID_UPDATE_REFUND_EXCHANGE("Invalid update refund exchange"),
  PROGRAM_DISCOUNT_NOT_FOUND("Program discount not found"),
  DESCRIPTION_ADMIN_NOT_FOUND("Description admin not found"),
  DISCOUNT_NAME_ALREADY_EXISTS("Discount name already exists"),
  NOT_FOUND_PRODUCT_SIDE("Not found product side"),
  PROGRAM_DISCOUNT_NAME_ALREADY_EXISTS("Program discount name already exists"),
  PRODUCT_HAS_DISCOUNT_COMBO("Product has discount combo"),
  PROGRAM_DISCOUNT_EXIST_NAME("Program discount exist name"),
  COMMITMENT_DUPLICATE_TYPE("Commitment duplicate type"),
  DISCOUNT_HOTSALE_EXIST_IN_VARIANT_IN_TIME("Discount hot sale exist in variant in time"),
  MODEL_NOT_FOUND("Model not found"),
  PRODUCT_MAIN_NOT_FOUND("Product main not found"),
  NOT_FOUND_CATEGORY("Not found category"),
  NOT_FOUND_PROGRAM_DISCOUNT_COMBO("Not found program discount combo"),
  NOT_FOUND_PROGRAM_DISCOUNT_HOT_SALE("Not found program discount hot sale"),
  LACK_MAX_MIN_PERCENTAGE_AMOUNT ("Lack max min percentage amount"),
  CATEGORY_NOT_PARAMTER("Category not parameter"),
  LACK_MAX_MIN_AMOUNT("Lack max min amount"),
  INVALID_PRODUCT_MAIN_ID_GIFT("Invalid product main id gift"),
  START_DATE_AFTER_END_DATE("Start date after end date"),
  PRODUCT_SIDE_NOT_FOUND("Product side not found"),
  INVALID_PRODUCT_MAIN_ID("Invalid product main id"),
  INVALID_PROGRAM_TYPE ("Invalid program type"),
  DISCOUNT_COMBO_GIFT_PRODUCT_MAIN_EXIST_IN_TIME("Discount combo gift product main exist in time"),
  INVALID_LIST_DISCOUNT_HOTSALE_ITEM_DTO("Invalid list discount hotsale item dto"),
  DUPLICATE_ROLE_NAME_OR_CODE("Duplicate role name or code"),
  DISCOUNT_AMOUNT_REQUIRED_FOR_VARIANT_ITEM ("Discount amount required for variant item"),
  DISCOUNT_PERCENTAGE_REQUIRED_FOR_VARIANT_ITEM("Discount percentage required for variant item"),
  EXIST_EMAIL("Exist email"),
  BRAND_EXISTS("Brand exists"),
  INVALID_CATEGORY("Invalid category"),
  REFRESH_TOKEN_NOT_VALID("Refresh token not valid"),
  TRANSACTION_STATUS_NOT_FOUND("Transaction status not found"),
  INVALID_END_DATE("Invalid end date"),
  PERCENTAGE_DISCOUNT_FIELDS_REQUIRED("Percentage discount fields required"),
  BRAND_NOT_FOUND("Brand not found"),
  BRAND_NAME_EXISTS("Brand name exists"),
  DISTRICT_NOT_FOUND("District not found"),
  ADDRESS_NOT_FOUND("Address not found"),
  INVALID_ADDRESS("Invalid address"),
  WARD_NOT_FOUND("Ward not found"),
  INVALID_TYPEPRODUCT("Invalid type product"),
  PROVINCE_NOT_FOUND("Province not found"),
  INVALID_DISCOUNT_TYPE("Invalid discount type"),
  INVALID_COMBO_DISCOUNT_TYPE("Invalid combo discount type"),
  PAYMENT_METHOD_NOT_FOUND("Payment method not found"),
  INVALID_COMBO_DISCOUNT("Invalid combo discount"),
  INVALID_VALUE_DISCOUNT_FIELDS("Invalid value discount fields"),
  DISCOUNT_AMOUNT_REQUIRED("Discount amount required"),
  INVALID_PERCENTAGE_DISCOUNT_AMOUNT("Invalid percentage discount amount"),
  INVALID_LIST_PRODUCT_ID("Invalid list product id"),
  INVALID_FOR_BUY_PRODUCT_GET_PRODUCT("Invalid for buy product get product id"),
  INVALID_LIST_PRODUCT_ID_GIFT_FOR_HOTSALE("Invalid list product id gift for hotsale"),
  INVALID_LIST_VARIANT_ID_FOR_HOTSALE("Invalid list variant id for hotsale"),
  INVALID_LIST_PRODUCT_ID_GIFT("Invalid list product id gift"),
  VARIANT_ALREADY_HAS_HOTSALE_DISCOUNT("Variant already has hotsale discount"),
  INVALID_INPUT("Invalid input"),
  IMAG_NOT_FOUND("Imag not found"),
  PERMISSION_NOT_FOUND("Permission not found"),
  INVALID_LIST_VARIANT_ID("Invalid list variant id"),
  DUPLICATE_PERMISSION_NAME("Duplicate permission name"),
  FILTER_ITEM_NOT_FOUND("Filter item not found"),
  DUPLICATE_MODEL_NAME("Duplicate model name"),
  ROLE_NOT_FOUND("Role not found"),
  DUPLICATE_ROLE_NAME("Duplicate role name"),
  ROLE_EXIST_WITH_LIST_PERMISSION("Role exist with list permission"),
  INVALID_ATTRIBUTE_ISFORVARIANT_IN_PARAMTER("Invalid attribute isForVariant in paramter"),
  INVALID_ATTRIBUTE_VALUE("Invalid attribute value"),
  DUPLICATE_TYPEPRODUCT("Duplicate type product"),
  ORDER_NOT_CHANGED("Order not changed"),
  CATEGORY_SELECTION_INVALID("Category selection invalid"),
  INTERNAL_SERVER_ERROR("Internal server error"),
  REVIEW_NOT_FOUND("Review not found"),
  REVIEW_EXISTS("Review exists"),
  NOT_FOUND_EMAIL("Not found email"),
  INVALID_LIST_ATTRIBUTE_VALUES("Invalid list attribute values"),
  NOT_FOUND_ORDER_ITEM("Not found order item"),
  TYPEPRODUCT_NOT_FOUND("Typeproduct not found"),
  TYPE_PRODUCT_NOT_FOUND("Type product not found"),
  INVALID_DATA_TYPE("Invalid data type"),
  DUPLICATE_ATTRIBUTE_VALUE("Duplicate attribute value"),
  DUPLICATE_FILTER_NAME("Duplicate filter name"),
  FILTER_NAME_EXIST("Duplicate filter exist"),
  CATEGORY_MUST_NOT_CHANGE("Category must not change"),
  INVALID_ATTRIBUTE("Invalid attribute"),
  NOT_FOUND_CART_ITEM("Not found cart item"),
  INVALID_FILTER_TYPE("Invalid filter type"),
  ORDER_ITEM_NOT_FOUND("Order item not found"),
  CATEGORY_NAME_SIZE_INVALID("Category name size invalid"),
  INVALID_START_DATE("Invalid start date"),
  TYPEPRODUCT_NOT_NULL("Typeproduct not null"),

  CART_ITEM_IS_UNACTIVE("Cart item is unactive"),
  EMPTY_FILTER_LIST("Empty filter list"),
  USER_NAME_ALREADY_EXISTS("User name already exists"),
  INVALID_PARAMETER("Invalid parameter"),
  CATEGORY_ID_REQUIRED("Category id required"),
  NO_CATEGORY_WITH_LISTPARAMETER_FOUND("No category with listparameter found"),
  INVALID_QUANTITY("Invalid quantity"),
  CATEGORY_ALREADY_EXISTS("Category already exists"),
  ERROR_COVER_IMAGE("Error cover image"),
  NOT_MULTIPLE_CHOOSE_VALUE("Not multiple choose value"),
  DUPLICATE_NAME_ATTRIBUTE("Duplicate name attribute"),
  LACK_BRAND("Lack brand"),
  ATTRIBUTE_VALUE_HAS_EXISTED_IN_CATEGORY("Attribute value has existed in category"),
  ATTRIBUTE_VALUE_NOT_FOUND_IN_CATEGORY("Attribute value not found in category"),
  CART_ID_PRODUCT_ID_OR_VARIANT_ID_MISSING("Cart id, product id or variant id missing"),
  INVALID_CART_ITEM("Invalid cart item"),
  PARENT_CATEGORY_NOT_FOUND("Parent category not found"),
  IN_VALID_TYPE_PRODUCT("In valid type product"),
  NOT_FOUND_TYPE_PRODUCT("Not found type product"),
  ATTRIBUTE_ALREADY_EXISTS("Attribute already exists"),CATEGORY_NAME_REQUIRED("Category name required"),
  ATTRIBUTE_NOT_FOUND("Attribute not found"),

  PARAMETER_NAME_OR_INDEX_EXISTED("Parameter name or index existed"),
  LACK_TYPE_PRODUCT("Lack type product"),
  PRODUCT_NOT_FOUND("Product not found"),
  PRODUCT_NAME_ALREADY_EXISTS("Product name already exists"),
  PARAMETER_NOT_FOUND("Parameter not found"),
  GROUP_INDEX_MUST_INCREASE_BY_AT_LEAST_5("Group index must increase by at least 5"),
  ORDER_NOT_FOUND("Order not found"),
  DUPLICATE_VARIANT("Duplicate variant"),

  PARAMETER_NAME_EXISTED("Parameter name existed"),
  EMPTY_PARAMETER_LIST("Empty parameter list"),
  EMPTY_ATTRIBUTE_LIST("Empty attribute list"),
  USER_NOT_FOUND("User not found"),
  VARIANT_NOT_EMPTY("Variant not empty"),
  DUPLICATE_GROUP_INDEX("Duplicate group index"),

  LACK_DATA_TYPE("Lack data type"),
  INVALID_TYPE_PRODUCT("Invalid type product"),
  DUPLICATE_PARAMETER_NAME("Duplicate parameter name"),
  IMAGES_NOT_FOUND("Images not found"),
  ATTRIBUTE_VALUE_INVALID("Attribute value invalid"),
  PARAMETER_EXISTED_IN_CATEGORY("Parameter existed in category"),
  CATEGORY_NOT_TAG("Category not tag"),
  INVALID_BRAND("Invalid brand"),
  PARAMETER_NOT_FOUND_IN_CATEGORY("Parameter not found in category"),
  NOT_ENOUGH_STOCK("Not enough stock"),
  ATTRIBUTE_NOT_FOUND_IN_PARAMETER("Attribute not found in parameter"),
  CATEGORY_NAME_EXISTS("Category name exists"),
  EXCEED_MAX1BUY("Exceed max1buy"),
  ERROR_ATTRIBUTE_VALUE("Error attribute value"),
  INVALID_TAG("Invalid tag"),
  NO_MATCH_VALUE_IN_VARIANT_AND_PRODUCT("No match value in variant and product"),
  LACK_PARAMETER("Lack parameter"),
  LACK_ATTRIBUTE("Lack attribute"),
  CART_ITEM_NOT_FOUND("Cart item not found"),
  ATTRIBUTE_VALUE_NAME_ALREADY_EXISTS("Attribute value name already exists"),
  INVALID_ATTRIBUTE_VALUE_FORMAT("Invalid attribute value format"),
  UNSUPPORTED_ATTRIBUTE_TYPE("Unsupported attribute type"),
  ATTRIBUTE_VALUE_ALREADY_EXISTS("Attribute value already exists"),
  ATTRIBUTE_VALUE_NOT_FOUND("Attribute value not found"),
  ERROR_REL_VARIANT_VALUE_PRODUCT("Error rel variant value product"),
  NOT_FOUND_BRAND("Not found brand"),
  ATTRIBUTE_NOT_FOR_VARIANT("Attribute not for variant"),
  LACK_ATTRIBUTE_VALUE("Lack attribute value"),
  ERROR_ATTRIBUTE("Error attribute"),
  DUPLICATE_PARAMETER("Duplicate parameter"),
  LACK_ATTRIBUTE_REQUIRED("Lack attribute required"),
  ATTRIBUTE_REQUIRED_NEED_VALUE("Attribute required need value"),
  ATTRIBUTE_NOT_FOR_SELECT("Attribute not for select"),
  CATEGORY_HAS_TAG_NOT_FOUND("Category has tag not found"),
  NO_ATTRIBUTE_VALUE_NOT_FOUND("No attribute value not found"),
  INVALID_ATTRIBUTE_FOR_VARIANTS("Invalid attribute for variants"),
  INVALID_PRODUCT_VARIANT("Invalid product variant"),
  INVALID_CATEGORY_TAG("Invalid category tag"),
  ATTRIBUTE_NAME_ALREADY_EXISTS("Attribute name already exists"),
  USER_EMAIL_EXISTED("User email existed"),
  CART_NOT_FOUND("Cart not found"),
  PRODUCT_VARIANT_NOT_FOUND("Product variant not found"),
  ERROR_PARAMETER("Error parameter");


  public String val;

  private ErrorMessageOm(String label) {
    val = label;
  }

  @Override
  public String val() {
    return val;
  }
}
