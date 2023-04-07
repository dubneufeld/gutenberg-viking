package dub.spring.gutenberg.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface BookService {
	
	public Flux<Category> getLeaveCategories();
	
	// category slug, not name
	Flux<Book> allBooksByCategory(String categorySlug, String sortBy);
		
	Mono<Category> getCategory(String categorySlug);
	
	Mono<Book> getBookBySlug(String slug);
	
	Mono<Book> getBookByBookId(long bookId);
		
	// more advanced methods
	Flux<Book> getBooksBoughtWith(long bookId, int limit);
	
	Flux<Book> getBooksNotReviewed(long userId, int limit) throws ParseException;

	// search queries
	Flux<Book> searchBookByTitle(String searchString);	
	Flux<Book> searchBookByDescription(String searchString);
	Flux<Book> searchBookByTags(String searchString);
	
}
