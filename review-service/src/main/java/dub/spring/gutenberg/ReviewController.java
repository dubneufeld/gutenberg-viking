package dub.spring.gutenberg;

import static reactor.core.publisher.Mono.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.review.Review;
import dub.spring.gutenberg.api.review.ReviewVote;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.services.ReviewService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ReviewController {
	
	private final ReviewService reviewService;
	
	@Autowired
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	@PostMapping(
			value = "/review",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<Review> createReview(@RequestBody Review body) {
		
		return this.reviewService.createReview(body);
	}
	
	
	@GetMapping("/reviewsByBookId/{bookId}/sort/{sort}")
	public Flux<Review> getReviewsByBookId(
			@PathVariable("bookId") int bookId,
			@PathVariable("sort") String sort) {
		
		if (bookId < 0) {
			throw new InvalidInputException("Invalid bookId: " + bookId);
		}
		
		return this.reviewService.getReviewByBookId(bookId, sort);
	
	}
	
	@GetMapping("/reviewsByUserId")
	public Flux<Review> getReviewsByUserId(
			@RequestParam("userId") int userId) {
		
		if (userId < 0) {
			throw new InvalidInputException("Invalid userId: " + userId);
		}
		return this.reviewService.getReviewsByUserId(userId);
	
	}
	
	@GetMapping("/reviewByReviewId")
	public Mono<Review> getReviewsByGrunge(
			@RequestParam("userId") int userId,
			@RequestParam("bookId") int bookId) {
		
		return this.reviewService.getReviewById(bookId, userId)
				.switchIfEmpty(error(new NotFoundException("No review found" + userId + " " + bookId )));
	}
	
	@GetMapping("/bookRating/{bookId}")
	public Mono<Double> getBookRating(
			@PathVariable("bookId") int bookId) {
	
		return this.reviewService.getBookRating(bookId);
	}
	
	
	/**
	 * Add vote to the review identified by userId and bookId
	 * */
	@PostMapping(
			value = "/addVote",
			produces = "application/json",
			consumes = "application/json")
	public Mono<Boolean> addVote(
			@RequestBody ReviewVote body) {
	
			Mono<Boolean> success = this.vote(Mono.just(body));

			return success;
					
	}
	
	@DeleteMapping("/deleteAllReviews")
	public Mono<Void> deleteAllReviews() {
		return this.reviewService.deleteAllReviews();	
	}
	
	@DeleteMapping("/review")
	public Mono<Void> deleteReview(
							@RequestParam("userId") int userId,
							@RequestParam("bookId") int bookId) {
		return this.reviewService.deleteReviewById(bookId, userId);		
	}
	
	
	private Mono<Boolean> vote(Mono<ReviewVote> reviewVote) {
	
		return reviewVote.flatMap((s) -> {
			try {
				return reviewService.voteHelpful(s.getUserId(), s.getBookId(), s.getVoterId(), s.isHelpful());
			} catch (Exception e) {
				e.printStackTrace();
				return Mono.error(new RuntimeException("SATOR"));
			}
		});		
			
	}
}
