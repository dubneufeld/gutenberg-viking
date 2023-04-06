package dub.spring.gutenberg.services;

import dub.spring.gutenberg.api.book.Book;
import reactor.core.publisher.Flux;

public interface SearchService {

	public Flux<Book> searchByDescription(String searchString);
	public Flux<Book> searchByTags(String searchString);
	public Flux<Book> searchByTitle(String searchString);
	

}
