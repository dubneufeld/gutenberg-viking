package dub.spring.gutenberg.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Item;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.OrderState;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.exceptions.OrderNotFoundException;
import dub.spring.gutenberg.exceptions.UnknownServerException;
import reactor.core.publisher.Mono;


/**
 * Try to reduce code duplication by creating a unique function and call it in flatMap
 * */

@Service
public class OrderServiceImpl implements OrderService {

	private static final String RECALCULATE_TOTAL = "/recalculateTotal";
	private static final String UPDATE_ORDER = "/updateOrder"; 
	private static final String CREATE_ORDER = "/createOrder"; 
	private static final String EDIT_CART = "/editCart"; 
	private static final String ORDER_BY_KEY = "/orderByKey"; 
	private static final String ADD_BOOK_TO_ORDER = "/addBookToOrder"; 
	private static final String GET_ACTIVE_ORDER = "/activeOrder"; 
	private static final String CHECKOUT_ORDER = "/checkoutOrder"; 
	private static final String SET_ORDER_STATE = "/setOrderState"; 
	
	
	@Autowired
	private WebClient orderSslClient;
	
	@Autowired
	private WebClient compositeSslClient;
		
	
	@Override
	public Mono<Order> updateOrder(Order order) {
		// take this implementation as a reference
		
		WebClient.ResponseSpec enclume = orderSslClient
				.method(HttpMethod.POST)
				.uri(UPDATE_ORDER)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(order), Order.class)
				.retrieve();
		Mono<ResponseEntity<Order>> forge = enclume.toEntity(Order.class);
			
		Mono<Order> grunge = forge.flatMap(catchErrorsAndTransform2);
	
		return grunge;
	}

	
	@Override
	public Mono<Order> createOrder(Order order) {
		
		WebClient.ResponseSpec enclume = orderSslClient
						.method(HttpMethod.POST)
						.uri(CREATE_ORDER)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(Mono.just(order), Order.class)
						.retrieve();
				
		System.out.println("OrderService createOrder");
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}

	
	@Override
	public Mono<Order> addBookToOrder(OrderAndBook orderAndBook) {
		
				
		WebClient.ResponseSpec enclume = compositeSslClient
						.method(HttpMethod.POST)
						.uri(ADD_BOOK_TO_ORDER)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(Mono.just(orderAndBook), OrderAndBook.class)
						.retrieve();
				
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}

	
	/** caution: getActiveOrder may return null initially */
	@Override
	public Mono<Order> getActiveOrder(long userId) {
		
		WebClient.ResponseSpec enclume = orderSslClient
				.method(HttpMethod.GET)
				.uri(GET_ACTIVE_ORDER + "/" + userId)
				.retrieve();
		
		Mono<Order> order = enclume.bodyToMono(Order.class);
		return order;
	}

	@Override
	public Mono<Order> checkoutOrder(OrderKey orderKey) {
		// only set state field to PRE_AUTHORIZE	
		
		OrderAndState orderAndState = new OrderAndState(
												orderKey.getUserId(), 
												orderKey.getDate(), 
												OrderState.PRE_AUTHORIZE);
		WebClient.ResponseSpec enclume = compositeSslClient
				.method(HttpMethod.POST)
				.uri(SET_ORDER_STATE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(orderAndState), OrderAndState.class)
				.retrieve();
		
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}

	
	@Override
	public Mono<Order> setCart(OrderKey orderKey) {
		
		OrderAndState orderAndState = new OrderAndState(orderKey.getUserId(), orderKey.getDate(), OrderState.CART);
			
		WebClient.ResponseSpec enclume = orderSslClient
				.method(HttpMethod.POST)
				.uri(SET_ORDER_STATE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.body(Mono.just(orderAndState), OrderAndState.class)
				.retrieve();	
		
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}
	
	@Override
	public Mono<Order> getOrderByOrderKey(OrderKey orderKey) {
			
		return this.retrieveOrderByOrderKey(orderKey);
	}
	
	private Mono<Order> retrieveOrderByOrderKey(OrderKey orderKey) {
		
		WebClient.ResponseSpec enclume = orderSslClient
				.method(HttpMethod.POST)
				.uri(ORDER_BY_KEY)
				.body(Mono.just(orderKey), OrderKey.class)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve();
		
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}
	

	@Override
	public Mono<Order> setOrderState(OrderAndState orderAndState) {
				
		WebClient.ResponseSpec enclume = compositeSslClient
				.method(HttpMethod.POST)
				.uri(SET_ORDER_STATE)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(orderAndState), OrderAndState.class)
				.retrieve();
			
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
	}
		
	
	@Override
	public Mono<Order> finalizeOrder(Order order, Address shippingAddress, PaymentMethod payMeth) {
		/** not an HTTP request
		 * only set order state to PRE_SHIPPING  
		*/
	
		order.setState(OrderState.PRE_SHIPPING);
		order.setPaymentMethod(payMeth);
		order.setShippingAddress(shippingAddress);
			
		return Mono.just(order);
	}
	
	
	Function<ResponseEntity<Order>, Mono<Order>> catchErrorsAndTransform2 = 
			(ResponseEntity<Order> clientResponse) -> {
						
				if (clientResponse.getStatusCode().is5xxServerError()) {
					throw new UnknownServerException();
				} else if (clientResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					throw new OrderNotFoundException();
				} else {
					return Mono.just(clientResponse.getBody());
				}
				
	};


	@Override
	public Mono<Order> editOrder(OrderKey orderKey, List<Item> items) {
		// encapsulation
		EditCart editCart = new EditCart(
									orderKey.getUserId(), 
									orderKey.getDate(), 
									items);
				
		WebClient.ResponseSpec enclume = compositeSslClient
						.method(HttpMethod.POST)
						.uri(EDIT_CART)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.body(Mono.just(editCart), EditCart.class)
						.retrieve();
					
				return enclume
						.toEntity(Order.class)
						.flatMap(catchErrorsAndTransform2)
						;	
		
	}


	@Override
	public Mono<Order> recalculateOrder(OrderKey orderKey) {
		
		WebClient.ResponseSpec enclume = compositeSslClient
				.method(HttpMethod.POST)
				.uri(RECALCULATE_TOTAL)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(orderKey), OrderKey.class)
				.retrieve();
			
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
		
	}


	@Override
	public Mono<Order> editOrder(Order order, List<Item> items) {
	
		EditCart editCart = new EditCart(
										order.getUserId(), 
										order.getDate(), 
										items);
							
		WebClient.ResponseSpec enclume = compositeSslClient
								.method(HttpMethod.POST)
								.uri(EDIT_CART)
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.body(Mono.just(editCart), EditCart.class)
								.retrieve();
							
		return enclume
				.toEntity(Order.class)
				.flatMap(catchErrorsAndTransform2)
				;	
		
	}


	@Override
	public Mono<Order> addBookToOrderLocal(Order order, Book book) {
		order.getLineItems().add(new Item(book.getBookId(),1));
		return Mono.just(order);
	}
	
}
