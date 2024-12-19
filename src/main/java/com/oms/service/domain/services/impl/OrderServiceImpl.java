package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.Payment.InitPaymentDto;
import com.oms.service.app.dtos.OrderDto.OrderDto;
import com.oms.service.app.dtos.OrderDto.OrderFilterDto;
import com.oms.service.app.dtos.OrderDto.OrderUpdateDto;
import com.oms.service.app.dtos.ProductSaleDto;
import com.oms.service.app.response.*;
import com.oms.service.app.response.Order.OrderItemResponse;
import com.oms.service.app.response.Order.OrderResponse;
import com.oms.service.app.response.Order.ProductSaleResponse;
import com.oms.service.domain.Utils.VnPayUtil;
import com.oms.service.domain.entities.*;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Address.Address;
import com.oms.service.domain.entities.Cart.Cart;
import com.oms.service.domain.entities.Cart.CartItem;
import com.oms.service.domain.entities.Order.*;
import com.oms.service.domain.entities.Payment.PaymentMethod;
import com.oms.service.domain.entities.Product.Images;
import com.oms.service.domain.entities.Product.Product;
import com.oms.service.domain.entities.Product.ProductVariant;
import com.oms.service.domain.enums.StateOrder;
import com.oms.service.domain.enums.StateOrderItem;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.*;
import com.oms.service.domain.repositories.Address.AddressRepository;
import com.oms.service.domain.repositories.Cart.CartItemRepository;
import com.oms.service.domain.repositories.Cart.CartRepository;
import com.oms.service.domain.repositories.Order.OrderItemRepository;
import com.oms.service.domain.repositories.Order.OrderRepository;
import com.oms.service.domain.repositories.Payment.PaymentMethodRepository;
import com.oms.service.domain.services.OrderService;
import com.oms.service.domain.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.oms.service.domain.Utils.Constant.CASH;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductVariantRepository productVariantRepository;
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;

	private final CartItemRepository cartItemRepository;
	private final OrderItemRepository orderItemRepository;
	private final PaymentService paymentService;

	private final PaymentMethodRepository paymentMethodRepository;
	private final AddressRepository addressRepository;

	@Override
	@Transactional
	public OrderResponse createOrderCart(Long id, OrderDto orderCartDto, HttpServletRequest request){
		//tôí ưu nó thành 2 câu query
		Address address=addressRepository.findByIdAndUserId(orderCartDto.getAddressId(),id);
		if(address==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val());
		}
		Cart cart=cartRepository.findCartByUserId(id);
		if(cart==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CART_NOT_FOUND.val());
		}
		List<Long> listCartItemId=cart.getListCartItems().stream().map(CartItem::getId).toList();
		if (!listCartItemId.containsAll(orderCartDto.getListCartItemId())) {
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_CART_ITEM.val());
		}
		PaymentMethod paymentMethod = paymentMethodRepository.findById(orderCartDto.getPaymentMethodId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PAYMENT_METHOD_NOT_FOUND.val()));
		Order order=null;
		if(orderCartDto.getOrderId()!=null){
			Order orderCheck=orderRepository.findById(orderCartDto.getOrderId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
			order=orderCheck;
			order.setPaymentMethodId(orderCheck.getPaymentMethodId());
		}
		else{
			order = new Order();
		}
		Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
		BigDecimal totalPrice = new BigDecimal(0);

		List<Long> listProductId=cart.getListCartItems().stream()
				.filter(cartItem->listCartItemId.contains(cartItem.getId()))
				.map(CartItem::getProduct)
				.map(Product::getId)
				.toList();
		productRepository.findByIdAndLock(listProductId);

		//
		modelMapper.typeMap(ProductVariant.class, ProductVariantInfo.class).addMappings(mapper -> {
			mapper.map(ProductVariant::getListRelVariantValueProduct, ProductVariantInfo::setListAttributeValue);
		});
		modelMapper.typeMap(RelVariantValueProduct.class, AttributeValueResponse.class).addMappings(mapper -> {
			mapper.map(source -> source.getAttributeValue().getAttValue().getAttValueString(), AttributeValueResponse::setAttValueString);
		});
		for(Long cartItemId:orderCartDto.getListCartItemId()){
			CartItem cartItem=cart.getListCartItems().stream().filter(cartItem1 -> cartItem1.getId().equals(cartItemId)).findFirst().orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CART_ITEM_NOT_FOUND.val()));
			Integer cartItemQuantity=cartItem.getQuantity();
			Integer variantQuantity=cartItem.getProductVariant().getQuantity();
			ProductVariant productVariant=cartItem.getProductVariant();

			if (variantQuantity < cartItemQuantity) {
				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_ENOUGH_STOCK.val());
			}
			cartItem.getProductVariant().setQuantity(variantQuantity - cartItemQuantity);
			createOrderItemToOrder(order, cartItem.getQuantity(), productVariant,cartItem.getProduct().getId(),currentTime);
			cartItem.setDeleted(true);
			productVariant.setQuantity(variantQuantity - cartItemQuantity);
			totalPrice = totalPrice.add(productVariant.getPrice().multiply(BigDecimal.valueOf(cartItemQuantity)));
		}
		UserInfo userInfo=new UserInfo();
		userInfo.setUserId(id);
		userInfo.setEmail(address.getUser().getEmail());
		userInfo.setFullName(address.getUser().getUsername());
		userInfo.setPhone(address.getUser().getPhone());
		userInfo.setAddress(address.getAddressDetail());
		userInfo.setProvince(address.getProvince().getName());
		userInfo.setDistrict(address.getDistrict().getName());
		userInfo.setWard(address.getWard().getName());

		order.setUserInfo(userInfo);
		order.setTotalPrice(totalPrice);
		order.setStateOrder(StateOrder.PENDING);
		order.setCreatedAt(currentTime);
		order.setDeleted(false);
		order.setPaymentMethodId(paymentMethod.getId());

		//
		createLogOrder(order,currentTime,StateOrder.PENDING);
		//
		order.setUser(cart.getUser());
		

		cartItemRepository.saveAllById(orderCartDto.getListCartItemId(),true);
		if(paymentMethod.getName().equalsIgnoreCase(CASH)){
			order.setStateOrder(StateOrder.SUCCESS);
			orderRepository.save(order);
			return convertToOrderResponse(order);
		}

		order.setStateOrder(StateOrder.PENDING);
		orderRepository.save(order);
		InitPaymentDto initPaymentDto=InitPaymentDto.builder()
				.amount(totalPrice.longValue())
				.txnRef(order.getId().toString())
				.ipAddress(VnPayUtil.getIpAddress(request))
				.build();
		OrderResponse orderResponse= convertToOrderResponse(order);
		String url=paymentService.createVnPayUrl(initPaymentDto);


		orderResponse.setVnpayUrl(paymentService.createVnPayUrl(initPaymentDto));

		return orderResponse;
	}


	private void createOrderItemToOrder(Order order, int quantity, ProductVariant productVariant,Long productId,Timestamp currentTime) {

		//
		ProductVariantInfo productVariantInfo=modelMapper.map(productVariant, ProductVariantInfo.class);
		productVariantInfo.setPrice(productVariant.getPrice());
		//

		ProductInfo productInfo = ProductInfo.builder()
				.name(productVariant.getProduct().getName())
				.image(productVariant.getProduct().getImages().stream().filter(Images::getIsCover).findFirst().get().getUrl())
				.productVariantInfo(productVariantInfo)
				.build();

		//
		OrderItem orderItem = new OrderItem();

		orderItem.setDeleted(false);
		orderItem.setProductId(productId);
		orderItem.setProductVariantId(productVariant.getId());
		orderItem.setCreatedAt(currentTime);
		orderItem.setProductInfo(productInfo);
		orderItem.setTotalPrice(productVariant.getPrice().multiply(BigDecimal.valueOf(quantity)));
		orderItem.setQuantity(quantity);
		orderItem.setStateOrderItem(StateOrderItem.PENDING);
		//
		order.addOrderItem(orderItem);
	}


	@Override
	@Transactional
	public ResponsePage<Order, OrderResponse> getAllOrdersByUser(Long userId, OrderFilterDto orderFilterDto, Pageable pageable) {
		validateStartDate(orderFilterDto.getStartDate(), orderFilterDto.getEndDate());
		User user=userRepository.findById(userId).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.USER_NOT_FOUND.val()));
		Page<Order> order=orderRepository.findAllOrder(userId,orderFilterDto.getStateOrder(),orderFilterDto.getIsPayment(),orderFilterDto.getMonth(),orderFilterDto.getYear(),orderFilterDto.getStartDate(),orderFilterDto.getEndDate(), pageable);
		return new ResponsePage<>( order, OrderResponse.class);
	}

	@Override
	@Transactional
	public ResponsePage<Order, OrderResponse> getAllOrders( OrderFilterDto orderFilterDto, Pageable pageable) {
		validateStartDate(orderFilterDto.getStartDate(), orderFilterDto.getEndDate());
		Page<Order> order=orderRepository.findAllOrder(null,orderFilterDto.getStateOrder(),orderFilterDto.getIsPayment(),orderFilterDto.getMonth(),orderFilterDto.getYear(),orderFilterDto.getStartDate(),orderFilterDto.getEndDate(), pageable);
		return new ResponsePage<>(order, OrderResponse.class);
	}

	@Override
	public OrderResponse updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
		Timestamp currentTime=Timestamp.valueOf(LocalDateTime.now());
		Order order=orderRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
		if(orderUpdateDto.getStateOrder()!=null&&order.getStateOrder().equals(orderUpdateDto.getStateOrder())
				&&orderUpdateDto.getIsPayment()!=null&&order.getIsPayment().equals(orderUpdateDto.getIsPayment())){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_CHANGED.val());
		}
		if(orderUpdateDto.getStateOrder()!=null){
			order.setStateOrder(orderUpdateDto.getStateOrder());
		}
		if(orderUpdateDto.getIsPayment()!=null){
			order.setIsPayment(orderUpdateDto.getIsPayment());
		}
		order.setUpdatedAt(currentTime);

		createLogOrder(order,currentTime,orderUpdateDto.getStateOrder());

		orderRepository.save(order);
		return modelMapper.map(order, OrderResponse.class);
	}

	public OrderItemResponse updateOrderItem(Long id, StateOrderItem stateOrderItem){
		OrderItem orderItem=orderItemRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_ITEM_NOT_FOUND.val()));
		orderItem.setStateOrderItem(stateOrderItem);
		orderItem.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
		orderItemRepository.save(orderItem);

		return modelMapper.map(orderItem, OrderItemResponse.class);
	}

	@Override
	public OrderResponse getOrderDetail(Long id) {
		Order order=orderRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
		return convertToOrderResponse(order);
	}

	@Override
	public OrderResponse deleteOrder(Long id) {
		Order order=orderRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
		Timestamp currentTime=Timestamp.valueOf(LocalDateTime.now());

		order.setDeleted(true);
		order.setUpdatedAt(currentTime);

		createLogOrder(order,currentTime,StateOrder.DELETED);

		orderRepository.save(order);
		return OrderResponse.builder().id(order.getId()).build();
	}

	@Override
	@Transactional
	public void deleteAllOrdersByUser(Long id) {
		User user=userRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.USER_NOT_FOUND.val()));
		Timestamp currentTime=Timestamp.valueOf(LocalDateTime.now());
		List<Order> listOrder=user.getListOrders();
		for(Order order:listOrder){
			order.setDeleted(true);
			order.setUpdatedAt(currentTime);
			createLogOrder(order,currentTime,StateOrder.DELETED);
		}
		orderRepository.saveAll(listOrder);
	}

	@Override
	@Transactional
	public ProductSaleResponse getSaleProduct(Long id, OrderFilterDto orderFilterDto){
		validateStartDate(orderFilterDto.getStartDate(), orderFilterDto.getEndDate());
		Product product=productRepository.findById(id).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.PRODUCT_NOT_FOUND.val()));
		ProductSaleDto productSaleDto = orderRepository.getSales(id,orderFilterDto.getStateOrder(), orderFilterDto.getMonth(), orderFilterDto.getYear(), orderFilterDto.getStartDate(), orderFilterDto.getEndDate());
		return ProductSaleResponse.builder()
				.totalPrice(productSaleDto.getPrice())
				.totalQuantity(productSaleDto.getQuantity())
				.productId(id)
				.build();
	}
	public void validateStartDate(LocalDate startDate, LocalDate endDate) {
		if(startDate!=null&&endDate!=null&&startDate.isAfter(endDate)){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.INVALID_START_DATE.val());
		}
	}
	public void createLogOrder(Order order,Timestamp currentTime,StateOrder stateOrder){
		LogOrder orderStateHistory = new LogOrder();
		orderStateHistory.setNewStateOrder(stateOrder);
		orderStateHistory.setOldStateOrder(order.getStateOrder());
		orderStateHistory.setDeleted(false);
		orderStateHistory.setCreatedAt(currentTime);
		order.addOrderStateHistory(orderStateHistory);
	}
	public OrderResponse convertToOrderResponse(Order order) {
		modelMapper.typeMap(Order.class, OrderResponse.class).addMappings(mapper -> {
			mapper.map(Order::getUserInfo,OrderResponse::setUserInfo);
		});
		return modelMapper.map(order, OrderResponse.class);
	}
}


//@Override
//@Transactional
//public OrderResponse createOrderCart(Long id, OrderDto orderCartDto, HttpServletRequest request){
//	//check cart
//	Cart cart=cartRepository.findCartByUserId(id);
//	if(cart==null){
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CART_NOT_FOUND.val());
//	}
//	//check address
//	Address address=addressRepository.findByIdAndUserId(orderCartDto.getAddressId(),id);
//	if(address==null){
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.ADDRESS_NOT_FOUND.val());
//	}
//	//check cart item
//	List<Long> listCartItemId=cart.getListCartItems().stream().map(CartItem::getId).toList();
//	if (!listCartItemId.containsAll(orderCartDto.getListCartItemId())) {
//		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_CART_ITEM.val());
//	}
//
//	PaymentMethod paymentMethod = paymentMethodRepository.findById(orderCartDto.getPaymentMethodId())
//			.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.PAYMENT_METHOD_NOT_FOUND.val()));
//	//
//	Order order=null;
//	Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
//
//	//thanh toán lại
//	if(orderCartDto.getOrderId()!=null){
//		Order orderCheck=orderRepository.findById(orderCartDto.getOrderId()).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST,ErrorMessageOm.ORDER_NOT_FOUND.val()));
//		order=orderCheck;
//		order.setPaymentMethodId(orderCheck.getPaymentMethodId());
//		order.setCreatedDate(currentTime);
//	}
//	//thanh toán mới
//	else{
//		order = new Order();
//	}
//	BigDecimal totalPrice = new BigDecimal(0);
//
//	List<Long> listProductVariantId=cart.getListCartItems().stream()
//			.filter(cartItem->listCartItemId.contains(cartItem.getId()))
//			.map(CartItem::getProductVariant)
//			.map(ProductVariant::getId)
//			.toList();
//	productVariantRepository.findByIdAndLock(listProductVariantId);
//
//	//set up mapper
//	modelMapper.typeMap(ProductVariant.class, ProductVariantInfo.class).addMappings(mapper -> {
//		mapper.map(ProductVariant::getListRelVariantValueProduct, ProductVariantInfo::setListAttributeValue);
//	});
//	modelMapper.typeMap(RelVariantValueProduct.class, AttributeValueResponse.class).addMappings(mapper -> {
//		mapper.map(source -> source.getAttributeValue().getAttValue().getAttValueString(), AttributeValueResponse::setAttValueString);
//	});
//	if(paymentMethod.getName().equalsIgnoreCase(CASH)){
//		for(Long cartItemId:orderCartDto.getListCartItemId()){
//			CartItem cartItem=cart.getListCartItems().stream().filter(cartItem1 -> cartItem1.getId().equals(cartItemId)).findFirst()
//					.orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.CART_ITEM_NOT_FOUND.val()));
//			Integer cartItemQuantity=cartItem.getQuantity();
//			Integer variantQuantity=cartItem.getProductVariant().getQuantity();
//
//			ProductVariant productVariant=cartItem.getProductVariant();
//
//			if (variantQuantity < cartItemQuantity) {
//				throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_ENOUGH_STOCK.val());
//			}
//			createOrderItemToOrder(order, cartItem.getQuantity(), productVariant,cartItem.getProduct().getId(),currentTime);
//
//			cartItem.setDeleted(true);
//			productVariant.setQuantity(variantQuantity - cartItemQuantity);
//			totalPrice = totalPrice.add(productVariant.getPrice().multiply(BigDecimal.valueOf(cartItemQuantity)));
//		}
//	}
//
//	UserInfo userInfo=new UserInfo();
//	userInfo.setUserId(id);
//	userInfo.setEmail(address.getUser().getEmail());
//	userInfo.setPhone(address.getUser().getPhone());
//	userInfo.setAddress(address.getAddressDetail());
//	userInfo.setProvince(address.getProvince().getName());
//	userInfo.setDistrict(address.getDistrict().getName());
//	userInfo.setWard(address.getWard().getName());
//
//	order.setUserInfo(userInfo);
//	order.setTotalPrice(totalPrice);
//	order.setStateOrder(StateOrder.PENDING);
//	if(orderCartDto.getOrderId()!=null){
//		order.setUpdatedAt(currentTime);
//
//	}
//	else{
//		order.setCreatedAt(currentTime);
//
//	}
//	order.setDeleted(false);
//	order.setPaymentMethodId(paymentMethod.getId());
//	order.setUser(cart.getUser());
//
//	//
//	createLogOrder(order,currentTime,StateOrder.PENDING);
//	//
//	cartItemRepository.saveAllById(orderCartDto.getListCartItemId(),true);
//	if(paymentMethod.getName().equalsIgnoreCase(CASH)){
//
//		order.setStateOrder(StateOrder.SUCCESS);
//		orderRepository.save(order);
//		return convertToOrderResponse(order);
//	}
//
//	order.setStateOrder(StateOrder.PENDING);
//	order.setCreatedDate(currentTime);
//	orderRepository.save(order);
//	InitPaymentDto initPaymentDto=InitPaymentDto.builder()
//			.amount(totalPrice.longValue())
//			.txnRef(order.getId().toString())
//			.ipAddress(VnPayUtil.getIpAddress(request))
//			.build();
//	OrderResponse orderResponse= convertToOrderResponse(order);
//	//tạo url cho thanh toán
//	orderResponse.setVnpayUrl(paymentService.createVnPayUrl(initPaymentDto,currentTime));
//
//	return orderResponse;
//}