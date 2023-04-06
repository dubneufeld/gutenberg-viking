package dub.spring.gutenberg.services;



import java.util.List;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.book.BookUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookService {
			
	public Mono<Book> createBook(Book body);
	
	public Mono<Book> getBookBySlug(String slug);
	
	public Mono<Book> getBookById(int id);
	
	public Mono<Void> deleteBook(int id);
	
	public Mono<Book> updateBookPrice(BookUpdate bookUpdate);
	
	public Mono<Void> deleteAllBooks();
	
	public Flux<Book> getAllBooksByCategory(String categorySlug, String sortBy);
	
	public Flux<Book> getAllBooksByBookIdList(List<Integer> bookIds);
			
}
