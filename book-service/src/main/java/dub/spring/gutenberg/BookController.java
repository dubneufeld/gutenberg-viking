package dub.spring.gutenberg;

import static reactor.core.publisher.Mono.error;

import java.util.Comparator;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.book.BookListWrap;
import dub.spring.gutenberg.api.book.BookSearch;
import dub.spring.gutenberg.api.book.BookUpdate;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;

import dub.spring.gutenberg.services.BookService;
import dub.spring.gutenberg.services.SearchService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BookController {
	
	private final BookService bookService;
	
	private final Comparator<Book> bookComp = new BookComp();
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}
	
	@GetMapping("/book/{id}")
	public Mono<Book> getBook(@PathVariable("id") int bookId) {
		
		if (bookId < 0) {
			throw new InvalidInputException("Invalid userId: " + bookId);
		}
		return this.bookService.getBookById(bookId)
				.switchIfEmpty(error(new NotFoundException("No book found" + bookId )));
				
	}
	
	@GetMapping("/book/slug/{slug}")
	public Mono<Book> getBookBySlug(@PathVariable("slug") String slug) {
		return this.bookService.getBookBySlug(slug)
				.switchIfEmpty(error(new NotFoundException("No book found" + slug )));
	}
	
	@PostMapping("/updateBook")
	public Mono<Book> updateBook(@RequestBody BookUpdate bookUpdate) {
		int bookId = bookUpdate.getBookId();
		if (bookId < 0) {
			throw new InvalidInputException("Invalid bookId: " + bookId);
		}
	
		return this.bookService.updateBookPrice(bookUpdate)
				.switchIfEmpty(error(new NotFoundException("No book found" + bookId )));
				
	}
	
	
	@PostMapping(
			value = "/book",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<Book> createBook(@RequestBody Book body) {
	
		return this.bookService.createBook(body);
	}
	
	@DeleteMapping("/deleteAllBooks")
	public Mono<Void> deleteAllBooks() {
		return this.bookService.deleteAllBooks();
	}
	
	@DeleteMapping("/book")
	public Mono<Void> deleteBook(@RequestParam("id") int id) {
		return this.bookService.deleteBook(id);
	}
	
	@GetMapping("/allBooksByCategory/{categorySlug}/sortBy/{sortBy}")
	public Flux<Book> allBooksByCategory(
			@PathVariable("categorySlug") String slug,
			@PathVariable("sortBy") String sortBy) {
		
		return this.bookService.getAllBooksByCategory(slug, sortBy);	
	}
	
	@PostMapping("/booksByBookIdList")
	public Flux<Book> allBooksByBookIdList(@RequestBody BookListWrap bookList) {
		
		return this.bookService.getAllBooksByBookIdList(bookList.getBookIds());	
	}
	
	@PostMapping("/searchByTitle")
	public Flux<Book> searchByTitle(@RequestBody BookSearch bookSearch) {
		
		final Mono<BookSearch> toto = Mono.just(bookSearch);

		Flux<Book> books = toto.flatMapMany(convertByTitle).sort(bookComp);		
		return books;
	}
	
	@PostMapping("/searchByTags")
	public Flux<Book> searchByTags(@RequestBody BookSearch bookSearch) {
		
		final Mono<BookSearch> toto = Mono.just(bookSearch);

		Flux<Book> books = toto.flatMapMany(convertByTags).sort(bookComp);		
		return books;
	}
	
	@PostMapping("/searchByDescription")
	public Flux<Book> searchByDescription(@RequestBody BookSearch bookSearch) {
		
		final Mono<BookSearch> toto = Mono.just(bookSearch);

		Flux<Book> books = toto.flatMapMany(convertByDescription).sort(bookComp);	;		
		return books;
	}
	
	
	Function<BookSearch, Flux<Book>> convertByTitle =
			s -> {		
				
					return searchService.searchByTitle(s.getSearchString());
	};
	
	Function<BookSearch, Flux<Book>> convertByDescription =
			s -> {		
					return searchService.searchByDescription(s.getSearchString());
	};
	
	Function<BookSearch, Flux<Book>> convertByTags =
			s -> {		
					return searchService.searchByTags(s.getSearchString());
	};
	

	static class BookComp implements Comparator<Book> {

		@Override
		public int compare(Book o1, Book o2) {
			return (o1.getBookId() < o2.getBookId()) ? -1 : ((o1.getBookId() > o2.getBookId())? 1 : 0);
		}
		
	}
}
