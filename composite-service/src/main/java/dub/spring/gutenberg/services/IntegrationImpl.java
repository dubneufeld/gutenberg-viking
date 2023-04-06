package dub.spring.gutenberg.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.composite.EditCart;
import dub.spring.gutenberg.api.order.Item;
import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.OrderState;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import dub.spring.gutenberg.api.review.Review;
import dub.spring.gutenberg.exceptions.OrderException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
public class IntegrationImpl implements Integration {

	private final String bookServiceUrl;
	private final String reviewServiceUrl;
	private final String orderServiceUrl;
	
	private final WebClient webClient;
	
	public IntegrationImpl(WebClient.Builder builder,
		    @Value("${app.book-service.host}") String bookServiceHost,
		      @Value("${app.book-service.port}") int    bookServicePort,

		      @Value("${app.review-service.host}") String reviewServiceHost,
		      @Value("${app.review-service.port}") int    reviewServicePort,

		      @Value("${app.order-service.host}") String orderServiceHost,
		      @Value("${app.order-service.port}") int    orderServicePort,

		      @Value("${app.user-service.host}") String userServiceHost,
		      @Value("${app.user-service.port}") int    userServicePort
	) {
		this.webClient = builder.build();
	    bookServiceUrl 		= "http://" + bookServiceHost +   ":" + bookServicePort;
        reviewServiceUrl 	= "http://" + reviewServiceHost + ":" + reviewServicePort;
        orderServiceUrl 	= "http://" + orderServiceHost +  ":" + orderServicePort;
	}
	
	@Override
	public Mono<Order> setOrderState(OrderKey orderKey, OrderState state) {
		
		// retrieve order
		Mono<Order> order = this.retrieve(orderKey);
		
		Mono<Order> grunge = order.flatMap(ord -> {
		
			OrderState oldState = ord.getState();
		
			// legal transitions only
			switch (state) {
			case CART:
				if (oldState.equals(OrderState.SHIPPED) ||
						oldState.equals(OrderState.PRE_SHIPPING)) {
					return Mono.error(new OrderException("Illegal state transition"));
				}
				break;
			case PRE_AUTHORIZE:
				if (oldState.equals(OrderState.SHIPPED) ||
						oldState.equals(OrderState.PRE_SHIPPING)) {
					return Mono.error(new OrderException("Illegal state transition"));
				}
				break;
			case PRE_SHIPPING:
				if (oldState.equals(OrderState.SHIPPED)) {
					return Mono.error(new OrderException("Illegal state transition"));
				}
				break;
			default:
				return Mono.error(new OrderException("Illegal state transition"));
			}
						
			// actual order state update
			ord.setState(state);
			
			// finally save order
			return this.persist(ord);
			
			
		});// grunge flatMap
		
		grunge.subscribe(g -> System.err.println("MORBUS " + g.getState()));
		
		return grunge;	
	}
	
	
	@Override
	public Mono<Order> recalculateTotal(OrderKey orderKey) {
		
		System.err.println("MORBUS GRAAVIS");
		// retrieve
		Mono<Order> order = this.retrieve(orderKey);
		
		// first step: from Mono<Order> to Mono<Order>
		Mono<Order> grunge = order.map(ord -> {
					// only legal if state = "CART"
					if (!( ord.getState().equals(OrderState.CART) || ord.getState().equals(OrderState.PRE_AUTHORIZE))) {
					
					throw new OrderException("illegal state");
					}
					System.err.println("POST MORTEM BEFORE recalculate");;
					return ord;
				});// flatMap grunge
		
		// second step: from Mono<Order> to Mono<Tuple2<Order,Integer>
		Mono<Tuple2<Order,Integer>> grouble = grunge.flatMap(doRecalculateSubtotal);
		
		// third step: from Mono<Tuple2<Order,Integer> to Mono<Order>
		Mono<Order> newOrder = grouble.flatMap(updateSubtotal);
		
		// finally persist order
		
		return newOrder.flatMap(ord -> {return persist(ord);});
		
	}
	
	@Override
	public Mono<Order> addBookToOrder(OrderKey orderKey, int bookId) {
		
		// retrieve order
		Mono<Order> order = this.retrieve(orderKey);
		
		// update order
		Mono<Order> grunge = order.flatMap(or -> {
			if (or.getState().equals(OrderState.CART)) {
				boolean present = false;			
				List<Item> items = or.getLineItems();
				for (Item item : items) {
					if (item.getBookId() == bookId) {
						present = true;
						item.setQuantity(item.getQuantity()+1);
					}
				}
				
				if (!present)  {
					// add a new Item
					items.add(new Item(bookId, 1));
				}
				
				or.setLineItems(items);
				
				// don't save order yet
				return Mono.just(or);
			} else {
				return Mono.error(new OrderException("Illegal state"));
			}
		});// grunge flatMap
		
		// recalculate order and persist
			
		// second step: from Mono<Order> to Mono<Tuple2<Order,Integer>
		Mono<Tuple2<Order,Integer>> grouble = grunge.flatMap(doRecalculateSubtotal);
				
		// third step: from Mono<Tuple2<Order,Integer> to Mono<Order>
		Mono<Order> newOrder = grouble.flatMap(updateSubtotal);
				
		// finally persist order		
		return newOrder.flatMap(ord -> {return persist(ord);});	
	}
	
	@Override
	public Flux<Integer> getBooksBoughtWith(int bookId) {
		String url = orderServiceUrl + "/booksBoughtWithBookId?bookId=" + bookId + "&outLimit=10";
		// first step: get all bookIds from order-service
		Flux<Integer> grunge = webClient.get().uri(url)		
	        		.retrieve().bodyToFlux(Integer.class).log();
		 
		grunge.subscribe(System.err::println);
		 
		return grunge;
		 
	} 
	
	@Override
	public Flux<Book> getBooksByBookIdList(BookListWrap bookList) {
		// second step get allBooks from book-service
		// Start from a Flux, avoid imperative style
		// create a Mono<List<Integer>> object
		// then use a flatMapMany
		 
	 	String bookUrl = bookServiceUrl + "/booksByBookIdList";
	 	Flux<Book> grunge = webClient.post().uri(bookUrl)
	 			.body(Mono.just(bookList), BookListWrap.class)		
     		.retrieve().bodyToFlux(Book.class).log();

	 	return grunge;	 	
	}
	
	// get all books shipped to a user
	@Override
	public Flux<Integer> getShippedBooksNotReviewed(UserAndReviewedBooks ua) {
		String url = orderServiceUrl + "/getBooksNotReviewed";
				   
		Flux<Integer> grunge = webClient.post().uri(url)
					   	.body(Mono.just(ua), UserAndReviewedBooks.class)		
		        		.retrieve().bodyToFlux(Integer.class).log();
			   	   
		return grunge.sort();
	} 
	
	@Override
	public Mono<Order> editCart(EditCart editCart) {
		
		OrderKey orderKey = new OrderKey(editCart.getUserId(), editCart.getDate());
			
		Mono<Order> order = this.retrieve(orderKey);
		
		// check order
		order.subscribe(or -> System.err.println("PUTIN "
						+ or.getDate() + " " + or.getState()));
		
		// from here on everything is functional
		
		// first step: from EditCart to Order
		Mono<Order> grunge = order.map(ord -> {
			// only legal if state = "CART"
			if (!ord.getState().equals(OrderState.CART)) {
				
			throw new OrderException("illegal state");
			}
			
			// set new Item list
			ord.setLineItems(editCart.getItems());
					
			return ord;
		});// flatMap grunge
		
		// second step: from Order to Tuple2<Order,Integer>
		// prepare recalculate
		Mono<Tuple2<Order,Integer>> grouble = grunge.flatMap(doRecalculateSubtotal);
			
		// third step
		// recalculate and persist to database
		Mono<Order> newOrder = grouble.flatMap(updateSubtotal);
		
		return newOrder.flatMap(ord -> {return persist(ord);});
	}
		
	@Override
	public Flux<Integer> getReviewsByUserId(long userId) {
		
        String url = reviewServiceUrl + "/reviewsByUserId?userId=" + userId;

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        Flux<Review> grunge = webClient.get().uri(url)
        		.retrieve().bodyToFlux(Review.class).log();//.onErrorResume(error -> empty());
		
        return grunge.map(r -> r.getBookId());
	}
	
	
	
	private Mono<Order> retrieve(OrderKey orderKey) {
		// retrieve
		String orderUrl = orderServiceUrl + "/orderByKey";
		System.err.println("POST MORTEM " + orderUrl);
		Mono<Order> order = webClient.post()
				.uri(orderUrl)
				.body(Mono.just(orderKey), OrderKey.class)
	     		.retrieve().bodyToMono(Order.class).log();
		System.err.println("RETRIEVE return");
		return order; 
	}
	
	
	Function<Tuple2<Order,Integer>,Mono<Order>> updateSubtotal =
			tuple -> {
				// update subtotal
				tuple.getT1().setSubtotal(tuple.getT2());
				
				System.err.println("POST MORTEM");
				
				return Mono.just(tuple.getT1());
	};// forge
	
	
	Function<Order, Mono<Tuple2<Order,Integer>>> doRecalculateSubtotal = 
			ord -> {
						
				// first build a List of bookIds
				List<Integer> lbookIds = new ArrayList<>();
				ord.getLineItems().forEach(it -> lbookIds.add(it.getBookId()));
												
				// make it more compact								
				Flux<Book> books = this.getBooksByBookIdList(new BookListWrap(lbookIds));					
				Flux<Integer> prices = books.map(book -> {return book.getPrice();});
						
				Flux<Integer> quantities = Flux.fromIterable(ord.getLineItems())
								.map(it -> {return it.getQuantity();});
						Flux<Tuple2<Integer, Integer>> grunges = Flux.zip(prices, quantities);
						Flux<Integer> groubles = grunges.map(gr -> {
							System.err.println("FOUTRE " + gr.getT1() + " "
									+ gr.getT2());
							return gr.getT1() * gr.getT2();
										});
						Mono<Integer> enclume = groubles.reduce(0, (x1, x2) -> {
							System.err.println("PUTIN " + x1 + " " + x2);
							return x1 + x2;});		
						
						return Mono.zip(Mono.just(ord), enclume);
			};// doRecalculateSubtotal		
	
	
	private Mono<Order> persist(Order order) {
		// persist
		String updateOrderUrl = orderServiceUrl + "/updateOrder";
		System.err.println("POST MORTEM");
		Mono<Order> newOrder = webClient.post()
				.uri(updateOrderUrl)	
				.body(Mono.just(order), Order.class)	
	     		.retrieve().bodyToMono(Order.class).log();
		return newOrder; 
	}

	@Override
	public Mono<Review> postReview(Review review) {
		String postReviewUrl = reviewServiceUrl + "/review";
	
		Mono<Review> newReview = webClient.post()
				.uri(postReviewUrl)	
				.body(Mono.just(review), Review.class)	
	     		.retrieve().bodyToMono(Review.class).log();
			
		return newReview;
	}
	
}
