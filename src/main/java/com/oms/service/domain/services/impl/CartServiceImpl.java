package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.CartDtoBase;
import com.oms.service.app.response.AttributeValueResponse;
import com.oms.service.app.response.Cart.CartItemResponse;
import com.oms.service.app.response.Cart.CartResponse;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.AttributeValue;
import com.oms.service.domain.entities.Cart.Cart;
import com.oms.service.domain.entities.Cart.CartItem;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.entities.RelVariantValueProduct;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Cart.CartItemRepository;
import com.oms.service.domain.repositories.Cart.CartRepository;
import com.oms.service.domain.repositories.ProductRepository;
import com.oms.service.domain.services.CartService;
import com.oms.service.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final ModelMapper mapper;

	private final UserService userService;
	@Override
	@Transactional
	public CartItemResponse addItemToCart(CartDtoBase cartDtoBase) {
		User user=userService.getAuthenticatedUser();
		Cart cart=cartRepository.findById(user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_NOT_FOUND.val()));
		Product product=productRepository.findById(cartDtoBase.getProductId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PRODUCT_NOT_FOUND.val()));
		ProductVariant productVariant = product.getListProductVariants().stream()
				.filter(variant -> variant.getId().equals(cartDtoBase.getProductVariantId()))
				.findFirst()
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PRODUCT_VARIANT_NOT_FOUND.val()));

//		if(productVariant.getQuantity()<cartDtoBase.getQuantity()){
//			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.NOT_ENOUGH_STOCK.val());
//		}
		CartItem cartItem=cart.getListCartItems().stream()
				.filter(cartItem1 -> cartItem1.getProductVariant().getId().equals(cartDtoBase.getProductVariantId()))
				.findFirst()
				.orElse(null);

		if(cartItem!=null){
			int updatedQuantity = cartItem.getQuantity() + 1;
			if (updatedQuantity > product.getMax1Buy()) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.EXCEED_MAX1BUY.val());
			}
			cartItem.setQuantity(updatedQuantity);
			cartItemRepository.save(cartItem);
			return CartItemResponse.builder().cartItemId(cartItem.getId()).quantity(cartItem.getQuantity()).build();
		}
		CartItem cartItemNew=new CartItem();
		cartItemNew.setQuantity(1);
		cartItemNew.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cartItemNew.setIsActive(true);
		cartItemNew.setDeleted(false);
		cartItemNew.setProduct(product);
		cartItemNew.setProductVariant(productVariant);

		cart.addCartitem(cartItemNew);
		cart.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cartRepository.save(cart);
		return CartItemResponse.builder().cartItemId(cartItemNew.getId()).quantity(cartItemNew.getQuantity()).build();
	}

	@Override
	@Transactional
	public CartItemResponse updateCartItem(Long cartItemId,Integer quantity) {
		User user=userService.getAuthenticatedUser();
		Cart cart=cartRepository.findById(user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_NOT_FOUND.val()));
		CartItem cartItem=cart.getListCartItems().stream()
				.filter(variant -> variant.getId().equals(cartItemId))
				.findFirst()
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_ITEM_NOT_FOUND.val()));
		if(!cartItem.getIsActive()){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_ITEM_IS_UNACTIVE.val());
		}
		if(cartItem.getProductVariant()==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PRODUCT_VARIANT_NOT_FOUND.val());
		}
		if(cartItem.getProductVariant().getQuantity()<quantity){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.NOT_ENOUGH_STOCK.val());
		}
		if(cartItem.getProduct()==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PRODUCT_NOT_FOUND.val());
		}
		cartItem.setQuantity(quantity);
		cartItem.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cartItemRepository.save(cartItem);
		return CartItemResponse.builder().cartItemId(cartItem.getId()).quantity(cartItem.getQuantity()).build();
	}

	@Override
	@Transactional
	public CartResponse getCart(){
		User user=userService.getAuthenticatedUser();
		Cart cart=cartRepository.findCartWithItemsIncludingDeletedProducts(user.getId());
		if(cart==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_NOT_FOUND.val());
		}
		cart.getListCartItems().sort(Comparator.comparing(CartItem::getCreatedAt));

		mapper.typeMap(Cart.class, CartResponse.class).addMappings(mapper -> {
			mapper.map(Cart::getListCartItems, CartResponse::setListCartItems);
		});
		mapper.typeMap(CartItem.class,CartItemResponse.class).addMappings(mapper -> {
			mapper.map(source->source.getProduct().getImages(),CartItemResponse::setImageUrl);
			mapper.map(src->src.getProductVariant().getPrice(),CartItemResponse::setPrice);
			mapper.map(source->source.getId(),CartItemResponse::setCartItemId);
			mapper.map(source->source.getProductVariant().getProduct().getMax1Buy(),CartItemResponse::setMax1Buy);
			mapper.map(source -> source.getProductVariant().getListRelVariantValueProduct(), CartItemResponse::setListAttributes);
		});

		mapper.typeMap(RelVariantValueProduct.class, AttributeValueResponse.class).addMappings(mapper -> {
			mapper.map(source->source.getAttributeValue().getId(),AttributeValueResponse::setId);
			mapper.map(src->src.getAttributeValue().getAttValue().getAttValueString(),AttributeValueResponse::setAttValueString);
			mapper.map(source -> source.getAttributeValue().getAttribute().getId(), AttributeValueResponse::setAttributeId);
		});
		return mapper.map(cart, CartResponse.class);
	}

	@Override
	@Transactional
	public CartItemResponse removeCartItem(Long cartItemId) {
		User user=userService.getAuthenticatedUser();
		Cart cart=cartRepository.findById(user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_NOT_FOUND.val()));
		CartItem cartItem=cart.getListCartItems().stream()
				.filter(cartItem1 -> cartItem1.getId().equals(cartItemId))
				.findFirst()
				.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_ITEM_NOT_FOUND.val()));
		cartItem.setDeleted(true);
		cart.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cartRepository.save(cart);
		return CartItemResponse.builder().cartItemId(cartItem.getId()).build();
	}
	@Override
	public void removeAllCartItem() {
		User user=userService.getAuthenticatedUser();
		Cart cart=cartRepository.findById(user.getId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.CART_NOT_FOUND.val()));
		for(CartItem cartItem : cart.getListCartItems()) {
			cartItem.setDeleted(true);
		}
		cart.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cartRepository.save(cart);
	}
}

