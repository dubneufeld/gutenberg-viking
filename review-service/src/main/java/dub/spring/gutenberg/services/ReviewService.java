package dub.spring.gutenberg.services;


import dub.spring.gutenberg.api.review.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {
			
	public Mono<Review> createReview(Review body);
	
	public Mono<Void> deleteReviewById(int bookId, int userId);	
	
	public Mono<Void> deleteAllReviews();	
	
	public Mono<Review> getReviewById(int bookId, int userId);	
	
	public Flux<Review> getReviewsByUserId(long userId);
	
	public Flux<Review> getReviewByBookId(
									int bookId, 
									String sortBy);
	
	public Mono<Double> getBookRating(int bookId);

	public Mono<Boolean> voteHelpful(long userId, int bookId, long voterId, boolean helpful);

	
}
