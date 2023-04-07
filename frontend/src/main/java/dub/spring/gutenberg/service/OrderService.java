package dub.spring.gutenberg.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Item;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.PaymentMethod;
import reactor.core.publisher.Mono;


//@PreAuthorize("hasRole('USER')")
public interface OrderService {
	
	@PreAuthorize("hasRole('USER')")
	Mono<Order> createOrder(Order order);
	
	
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	Mono<Order> updateOrder(Order order);
	
	Mono<Order> recalculateOrder(OrderKey orderKey);
	
	Mono<Order> editOrder(OrderKey orderKey, List<Item> items);
	Mono<Order> editOrder(Order order, List<Item> items);
	
	Mono<Order> getOrderByOrderKey(OrderKey orderKey);
	
	Mono<Order> getActiveOrder(long userId);// Not in PRE_SHIPPING or SHIPPED state
	
	@PreAuthorize("hasAuthority('ROLE_USER')")
	Mono<Order> addBookToOrder(OrderAndBook orderAndBook);
	
	Mono<Order> addBookToOrderLocal(Order order, Book book);
		
	//@PreAuthorize("hasAuthority('ROLE_USER')")
	Mono<Order> checkoutOrder(OrderKey orderKey);
	
	//Order setPreShipping(int orderId);
	
	Mono<Order> setOrderState(OrderAndState orderState);
	
	Mono<Order> setCart(OrderKey orderKey);
	//Order editOrder(OrderKey orderKey, List<Item> items);
	

	Mono<Order> finalizeOrder(Order order, Address shippingAddress, PaymentMethod payMeth);
}
