package dub.spring.gutenberg.services;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.composite.EditCart;
import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.OrderState;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import dub.spring.gutenberg.api.review.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Integration {
	
	public Mono<Review> postReview(Review review);

	public Mono<Order> setOrderState(OrderKey orderKey, OrderState state);

	public Mono<Order> recalculateTotal(OrderKey orderKey);
	
	public Mono<Order> addBookToOrder(OrderKey orderKey, int bookId);
	
	public Flux<Integer> getBooksBoughtWith(int bookId);
	
	public Flux<Book> getBooksByBookIdList(BookListWrap bookList); 
	
	// get all books shipped to a user
	public Flux<Integer> getShippedBooksNotReviewed(UserAndReviewedBooks ua);
	
	public Mono<Order> editCart(EditCart editCart);
	
	public Flux<Integer> getReviewsByUserId(long userId);

}
