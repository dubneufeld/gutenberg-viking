package dub.spring.gutenberg.controller.books;

import java.util.Collection;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Category;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.controller.reviews.ReviewController;
import dub.spring.gutenberg.controller.reviews.ReviewWithAuthor;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.ReviewService;
import dub.spring.gutenberg.service.UserService;
import dub.spring.gutenberg.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


@Controller
public class BookController {
	
	@Autowired
	private ReviewService reviewService;
	
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	

	
	@RequestMapping(value = "/allBooksByCategory/{categorySlug}",
			produces = "text/html; charset=utf-8")
	public Mono<Rendering> booksByCategory(@PathVariable("categorySlug") String categorySlug) {
			
		/** sort by title */		
		Flux<Book> books = bookService.allBooksByCategory(categorySlug, "title");
		
		Mono<Category> category = this.bookService.getCategory(categorySlug);
		
		return Mono.just(Rendering.view("books/allBooksByCategory")
				.modelAttribute("books", books)
				.modelAttribute("category", category)
				.build());
	}
	
	
	// use a redirection
	@RequestMapping("/sortBy")
	public String getBookSortHelpful(
							@RequestParam("field") String sortBy,
							final WebSession session) {
		
		session.getAttributes().put("sortBy", sortBy);
		
		String redirect = "redirect:/books/" + session.getAttribute("bookSlug");
		
		return redirect;
	}
	
	
	@RequestMapping("/books/{bookSlug}")
	public Mono<Rendering> getBook(@PathVariable String bookSlug, final WebSession session) {
			
		session.getAttributes().put("bookSlug", bookSlug);
		
		Mono<Book> book = bookService.getBookBySlug(bookSlug);
			
		Mono<Double> price = book.map(b -> b.getPrice()/100.0);
			
		String sortBy = (String)session.getAttributes().get("sortBy");
		if (sortBy == null)  {// default
			sortBy = "rating";
		}
		
		// first create a Tuple2
		Mono<Tuple2<Book,String>> revSource = Mono.zip(book, Mono.just(sortBy));
		
		Flux<Review> reviews = revSource.flatMapMany(b -> {
			Flux<Review> revs = this.reviewService.getReviewsByBookId(b.getT1().getBookId(), b.getT2());
			return revs;
		});
		
		// check first
		
		// then transform into Flux<ReviewWithAuthor>
			
		Flux<ReviewWithAuthor> rvas = reviews.flatMap(transformReviewWithAuthor);
		
		// check first		
		Mono<Double> bookRating = book.flatMap( b -> reviewService.getBookRating(b.getBookId()));
		
		return Mono.just(Rendering.view("books/book")
				.modelAttribute("book", book)
				.modelAttribute("price", price)
				.modelAttribute("rating", bookRating)
				.modelAttribute("reviews", rvas)
				.modelAttribute("voteForm", new ReviewController.VoteForm())
				.build());	
		
	}
	
	
	
	Function<Review, Mono<ReviewWithAuthor>> transformReviewWithAuthor =
			t -> {
				Mono<String> username = this.userService.findByUserId(t.getUserId())
						.map(u -> u.getUsername());
					
				Mono<ReviewWithAuthor> rva = username
						.map(n -> new ReviewWithAuthor(t, n));
						
				return rva;
	};
	

}
