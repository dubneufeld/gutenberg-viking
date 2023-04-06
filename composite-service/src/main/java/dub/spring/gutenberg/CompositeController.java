package dub.spring.gutenberg;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.composite.EditCart;
import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderAndBook;
import dub.spring.gutenberg.api.order.OrderAndState;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import dub.spring.gutenberg.api.review.Review;
import dub.spring.gutenberg.services.BookListWrap;
import dub.spring.gutenberg.services.Integration;
import dub.spring.gutenberg.services.ReviewWrap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

@RestController
public class CompositeController {
	
	private static final Logger LOG = 
			LoggerFactory.getLogger(CompositeController.class);

	private Integration integration;
		
	@Autowired
	public CompositeController(Integration integration) {
		this.integration = integration;
	}
	
	@PostMapping("/setOrderState")
	public Mono<Order> setOrderState(@RequestBody OrderAndState orderAndState) {
		
		return this.integration.setOrderState(new OrderKey(orderAndState.getUserId(), orderAndState.getDate()), orderAndState.getState());
	
	}

	@PostMapping("/addBookToOrder")
	public Mono<Order> addBookToOrder(@RequestBody OrderAndBook orderAndBook) {
		
		return integration.addBookToOrder(new OrderKey(orderAndBook.getUserId(), orderAndBook.getDate()), orderAndBook.getBookId());
	}
	
	// to be completed
	@PostMapping("/recalculateTotal")
	public Mono<Order> recalculateOrder(@RequestBody OrderKey orderKey) {
		
		Mono<Order> grunge = integration.recalculateTotal(orderKey);
			
		return grunge; 
	}
	
	@PostMapping("/review")
	public Mono<ReviewWrap> postReview(@RequestBody Review review) {
		
		Flux<Integer> booksNotReviewed = this.getBooksNotReviewed(review.getUserId(), 0);
				
		// create a Tuple2
		Mono<Tuple2<Review, Flux<Integer>>> sator = Mono.zip(Mono.just(review), Mono.just(booksNotReviewed));
		// check if review post is allowed
		Mono<Tuple2<Review, Boolean>> arepo = sator.flatMap(transMatch);
		
		// post only if allowed
		Mono<ReviewWrap> grinch = arepo.flatMap(transReview);
			
		return grinch;				
	}
	
	
	@PostMapping("/editCart")
	public Mono<Order> editCart(@RequestBody EditCart editCart) {
		
		return integration.editCart(editCart);
	}
	
	@GetMapping("/getBooksBoughtWithBookId")
	public Flux<Book> getBooksBoughtWithBookId(@RequestParam("bookId") int bookId) {
		// first step: get all bookIds
	
		Flux<Integer> bookIds = integration.getBooksBoughtWith(bookId);
	
		// second step
	
		Mono<List<Integer>> lbookIds = bookIds.collectList();
	
		Flux<Book> flux = lbookIds.flatMapMany(transformMono);
	
		return flux;
	
	}
	
	@GetMapping("/getBooksNotReviewed")
	public Flux<Integer> getBooksNotReviewed(@RequestParam("userId") long userId, @RequestParam("outLimit") int outLimit) {
		
		/** 
		 * First step: find all books 
		 * already reviewed by user referenced by userId 
		 * */
				
		Flux<Integer> bookIds = integration.getReviewsByUserId(userId);
			
		Mono<List<Integer>> lbookIds = bookIds.collectList();
			
		/** 
		 * second step: find all books 
		 * recently bought by user referenced by userId 
		 * that were not reviewed by user yet.
		 * this step is implemented on order server side
		 * we need to post a List<Integer> to order server
		 * it is easier here to post an encapsulating object
		 * the helper class is named UserAndReviewedBooks
		 * */
		
		// here lbookIds is a Mono<List<Integer>>
		// I create a Tuple
		
		Mono<Tuple3<List<Integer>,Long,Integer>> fourbi = Mono.zip(lbookIds, Mono.just(userId), Mono.just(outLimit));
		
		return fourbi.flatMapMany(transformTuple);
				
	}
	
	
	private Function<List<Integer>, Flux<Book>> transformMono = 
			t -> {
				// create a new BookListWrap object
				BookListWrap wrap = new BookListWrap();
				wrap.setBookIds(t);
				
				Flux<Book> books = integration.getBooksByBookIdList(wrap);
					
				return books;
	};
	
	
	private Function<Tuple3<List<Integer>,Long,Integer>, Flux<Integer>> transformTuple = 
			t -> {
				// create a new UserAndReviewedBooks object
				UserAndReviewedBooks ua = new UserAndReviewedBooks();
				ua.setReviewedBookIds(t.getT1());
				ua.setUserId(t.getT2());
				ua.setOutLimit(t.getT3());
				
				// send POST request to order-service
				Flux<Integer> forge = integration.getShippedBooksNotReviewed(ua);
								
				return forge;
														
	};
	
	

	private Mono<Review> doPostReview(Review review) {
		System.err.println("PUTIN doPostReview " + review.getText());
		Mono<Review> nRev = integration.postReview(review);
		return nRev;
		
	}
	
	private Function<Tuple2<Review,Flux<Integer>>, Mono<Tuple2<Review,Boolean>>> transMatch = 
			tt -> {
				System.err.println("transMatch");
				Predicate<Integer> matchP = i -> (i == tt.getT1().getBookId());			
				Flux<Integer> fl = tt.getT2().filter(matchP);
				return Mono.zip(Mono.just(tt.getT1()), fl.hasElements());	
	};
	
	
	
	
			
	Function<Tuple2<Review,Boolean>, Mono<ReviewWrap>> transReview =
		tt -> {
				if (tt.getT2()) {
					System.out.println("CHEVAL");
					Mono<Review> nRev = doPostReview(tt.getT1());			
					return nRev.map(e -> {return new ReviewWrap(e, true);});
				} else {
					System.out.println("LAPIN");
					return Mono.just(tt.getT1()).map(e -> {return new ReviewWrap(e, false);});	
				}
						
	};
	
	
}
