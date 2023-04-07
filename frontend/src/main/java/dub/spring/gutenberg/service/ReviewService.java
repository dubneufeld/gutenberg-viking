package dub.spring.gutenberg.service;

import java.util.List;
import java.util.Optional;

import dub.spring.gutenberg.api.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ReviewService {

	Mono<ReviewWrap> createReview(Review review);	
	
	Flux<Review> getReviewsByUserId(long userId);
	
	Flux<Review> getReviewsByBookId(
								long bookId, 
								String sortBy);
	
	Mono<Double> getBookRating(long bookId);
	
		
	// here voterId is the voter userId
	// a review is identified by a unique tuple (bookId, userId)
	Mono<Boolean> voteHelpful(ReviewVote reviewVote);
	Mono<Boolean> hasVoted(long bookId, long userId, long voterId);
	
}
