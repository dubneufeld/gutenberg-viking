package dub.spring.gutenberg.controller.books;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.service.BookSearch;
import dub.spring.gutenberg.service.BookService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
public class SearchController {

	@Autowired
	private BookService bookService;
	
	@PostMapping(
			value = "/search")
	public Mono<Rendering> searchBook(@ModelAttribute("bookSearch") BookSearch bookSearch) {
		
		Flux<Book> booksT = bookService.searchBookByTitle(bookSearch.getSearchString());
		Flux<Book> booksD = bookService.searchBookByDescription(bookSearch.getSearchString());
		Flux<Book> booksTag = bookService.searchBookByTags(bookSearch.getSearchString());
		
		// Here I want unique books
		
		Flux<Book> books = Flux.concat(booksT, booksD, booksTag);
		
		books = books.filter(distinctByBookId());
				
		Map<String, Object> params = new HashMap<>();
		params.put("books", books);
		
		return Mono.just(Rendering.view("books/searchResults")
				.modelAttribute("books", books)
				.build());
	}

	
	public Predicate<Book> distinctByBookId() {
  	    
		Map<Long, Boolean> seen = new ConcurrentHashMap<>(); 
		        
		return t -> seen.putIfAbsent(t.getBookId(), Boolean.TRUE) == null; 	
	}
	
}
