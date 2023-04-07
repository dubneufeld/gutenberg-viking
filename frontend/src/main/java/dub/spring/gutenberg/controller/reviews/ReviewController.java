package dub.spring.gutenberg.controller.reviews;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.service.BookSearch;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.ReviewService;
import dub.spring.gutenberg.service.ReviewWrap;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Controller
public class ReviewController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private UserService userService;
	
	

	@GetMapping(value ="/createReview/{bookSlug}")
	public Mono<Rendering> getReviewForm(@PathVariable String bookSlug, 
								
								Model model) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
						
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
		
		Mono<Book> book = bookService.getBookBySlug(bookSlug);
		Mono<Long> bookId = book
				.map(b -> b.getBookId());
		
		Mono<MyUser> user = username.flatMap(u -> this.userService.getProfile(u));
		Mono<Long> userId = user.map(us -> us.getUserId());
		
		Mono<Tuple2<Long,Long>> tuple = Mono.zip(bookId, userId);
		
		Mono<Review> review = tuple.map(t -> {
											Review r = new Review();
											r.setBookId(t.getT1());
											r.setUserId(t.getT2());
											return r;
											});
		
		
		return Mono.just(Rendering.view("reviews/createReview")
				.modelAttribute("book", book)
				.modelAttribute("review", review)
				.build())
				;
		
	}
	
	
	@PostMapping(
			value = "/createReview")
	public Mono<Rendering> postReview(@ModelAttribute("review") Review review) {
	
		review.setDate(LocalDateTime.now());
		
		// add review to database
		Mono<ReviewWrap> wrap = reviewService.createReview(review);
		
		return Mono.just(Rendering.view("reviews/createReviewSuccess")
				.modelAttribute("wrap", wrap)
				.build())
				;
	}
			
	
	public static class VoteForm {
		private long userId;// part of review key
		private long bookId;// part of review key, to be cleaned
		private boolean helpful = false;
		
		public VoteForm() {
		}

		public long getUserId() {
			return userId;
		}
		
		public void setUserId(long userId) {
			this.userId = userId;
		}

		public long getBookId() {
			return bookId;
		}
		
		public void setBookId(long bookId) {
			this.bookId = bookId;
		}

		public boolean isHelpful() {
			return helpful;
		}

		public void setHelpful(boolean helpful) {
			this.helpful = helpful;
		}
	}
}
