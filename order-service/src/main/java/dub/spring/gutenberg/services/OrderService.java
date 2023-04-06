package dub.spring.gutenberg.services;

import java.text.ParseException;
import java.time.LocalDateTime;

import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
	
	Flux<Integer> getBooksNotReviewed(UserAndReviewedBooks userAndReviewedBooks) 
											throws ParseException;
	
	Mono<Order> createOrder(Order order);
	
	Mono<Order> updateOrder(Order order);
	
	Mono<Order> getOrderByKey(long userId, LocalDateTime date);
	
	Mono<Order> getActiveOrder(long userId);// Not in PRE_SHIPPING or SHIPPED state

	Mono<Order> checkoutOrder(long orderId);
	
	Mono<Void> deleteAllOrders();
	
	// return orderIds, not bookIds
	Flux<OrderKey> getShippedOrdersByUserId(long userId);
	
	Flux<Integer> getBooksBoughtWithBookId(int bookId, int outLimit);
	
	
}
