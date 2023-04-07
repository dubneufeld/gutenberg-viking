package dub.spring.gutenberg.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class TestController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookService bookService;
	
	@GetMapping("/test/booksBoughtWithBookId")
	public Flux<Book> getBooksBoughtWith(@RequestParam long bookId) {
		
		return this.bookService.getBooksBoughtWith(bookId, 5);
	}
	
	
	@GetMapping("/test/booksNotReviewed/{userId}")
	public Flux<Book> getBooksNotReviewed(@PathVariable long userId) {
		
		try {
			return this.bookService.getBooksNotReviewed(userId, 5);
		} catch (ParseException e) {
			System.out.println("Exception caught");
			return Flux.empty();
		}
	}
	
	
	
	@GetMapping("/test/enclume")
	public String getEnclume() {
		
		return "ENCLUME";
	}
	
	@GetMapping("/test/user/{username}")
	public Mono<MyUser> getEnclume(@PathVariable("username") String username) {
		
		return this.userService.getProfile(username);

	}
	
	@GetMapping("/test/userDetails/{username}")
	public Mono<UserDetails> getUserDetails(@PathVariable("username") String username) {
		
		return this.userService.findByUsername(username);

	}

}
