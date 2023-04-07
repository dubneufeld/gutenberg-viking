package dub.spring.gutenberg.controller;

import java.text.ParseException;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.UserAuthority;
import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Category;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.service.BookSearch;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.UserService;
import dub.spring.gutenberg.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Controller
public class DefaultController {
	
	@Autowired
	BookService bookService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserUtils userUtils;
	
	
	@GetMapping({"/logout"})
	Mono<Rendering> logout(final WebSession webSession) {
		
		webSession.invalidate();
		
		return Mono.just(Rendering.view("index").build());
	}
	
	@GetMapping({"/", "/home", "/backHome"})
	Mono<Rendering> hello(final WebSession session) {
				
		Mono<MyUser> user = userUtils.getLoggedUser();
		Mono<String> username = user.map(a -> a.getUsername());
			
		Mono<Long> userId = user.map(u -> u.getUserId());
		
		Mono<UserDetails> details = username.flatMap(n -> this.userService.findByUsername(n));
		
		Mono<Collection< ? extends GrantedAuthority>> auths = details.map(u -> u.getAuthorities());
		
		// clean later
		Flux<Book> booksToReview = userId.flatMapMany(u -> {
			try {
				return bookService.getBooksNotReviewed(u, 5);
			} catch (ParseException e) {
				System.err.println("EXCEPTION");
				return Flux.empty();
			}
		});
			
		Flux<Category> categories = bookService.getLeaveCategories();
	 	  	
		return Mono.just(Rendering.view("index")
				.modelAttribute("auths", auths)
				.modelAttribute("username", username)
				.modelAttribute("bookSearch", new BookSearch())
				.modelAttribute("booksToReview", booksToReview)
				.modelAttribute("categories", categories)
				.build())
				;
	}
}
