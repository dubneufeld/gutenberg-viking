package dub.spring.gutenberg;


import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import dub.spring.gutenberg.services.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// avoid code duplication in OrderServiceImpl

@RestController
public class OrderController {
	
	private final OrderService orderService;
		
	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}
	
	
	@PostMapping("/enclume")
	public Mono<Order> getEnclume(@RequestBody OrderKey orderKey) {
		
		return this.orderService.getOrderByKey(orderKey.getUserId(), orderKey.getDate());	

	}
	

	
	@DeleteMapping("/deleteAllOrders")
	public Mono<Void> deleteAllOrders() {
		
		return this.orderService.deleteAllOrders();
	}
	
	
	@PostMapping("/updateOrder")
	public Mono<Order> updateOrder(@RequestBody Order order) {
	
		Mono<Order> toto = this.orderService.updateOrder(order);
		
		return toto;
	}
	
	@PostMapping("/orderByKey")
	public Mono<Order> getOrderByKey(@RequestBody OrderKey orderKey) {
		
		return this.orderService.getOrderByKey(orderKey.getUserId(), orderKey.getDate());	
	}
	
	
	@PostMapping("/createOrder")
	public Mono<Order> createOrder(@RequestBody Order order) {
		
		Mono<Order> nOrder = this.orderService.createOrder(order);
		
		return nOrder;		
	}
	
	
	
	@GetMapping("/activeOrder/{userId}")
	public Mono<Order> getActiveOrderAlt(@PathVariable long userId) {
		
		return this.orderService.getActiveOrder(userId);

	}
	

	@GetMapping("/shippedOrdersByUserId/{userId}")
	public Flux<OrderKey> getShippedOrdersByUserId(@PathVariable("userId") long userId) {
	
		Flux<OrderKey> grunge = this.orderService.getShippedOrdersByUserId(userId);	
		
		return grunge;
		
	}
	
	@GetMapping("/booksBoughtWithBookId")
	public Flux<Integer> getBooksBoughtWithBookId(@RequestParam("bookId") int bookId, @RequestParam("outLimit") int outLimit) {
		
		return this.orderService.getBooksBoughtWithBookId(bookId, outLimit);	
		
	}
	
	
	@PostMapping("/getBooksNotReviewed")
	public Flux<Integer> getBooksNotReviewed(@RequestBody UserAndReviewedBooks userAndReviewedBooks)  {
		
		try {
			return this.orderService.getBooksNotReviewed(userAndReviewedBooks);
		} catch (ParseException e) {
			
			throw new RuntimeException("grunge");
		}
		}
	


}
