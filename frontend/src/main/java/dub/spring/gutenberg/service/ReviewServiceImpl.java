package dub.spring.gutenberg.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.exceptions.UnknownServerException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ReviewServiceImpl implements ReviewService {
			
	public static final String CREATE_REVIEW = "/review";
	public static final String REVIEWS_BY_BOOK_ID = "/reviewsByBookId/";
	public static final String BOOK_RATING = "/bookRating/";
	public static final String REVIEW_BY_REVIEW_ID = "/reviewByReviewId/";
	public static final String REVIEWS_BY_USER_ID = "/reviewsByUserId";
	public static final String ADD_VOTE = "/addVote";
	public static final String SORT = "/sort/";
	

	@Autowired 
	private WebClient reviewSslClient;
	
	@Autowired 
	private WebClient compositeSslClient;
	
	
	
	@Override
	public Mono<ReviewWrap> createReview(Review review) {
			 
		WebClient.ResponseSpec enclume = compositeSslClient
			.method(HttpMethod.POST)
			.uri(CREATE_REVIEW)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(Mono.just(review), Review.class)
			.retrieve();
		  
		Mono<ReviewWrap> wrap = enclume.toEntity(ReviewWrap.class)
				 					.flatMap(catchErrorsAndTransformCreate);
		
		return wrap;
	}
	
	
	@Override
	public Flux<Review> getReviewsByBookId(long bookId, String sortBy) {

		WebClient.RequestBodySpec requestSpec = reviewSslClient
				.method(HttpMethod.GET)
				.uri(REVIEWS_BY_BOOK_ID + bookId + SORT + sortBy);
		
		WebClient.ResponseSpec response = requestSpec.retrieve();
		Flux<Review> flux  = response.bodyToFlux(Review.class);
		
		return flux;
	
	}

	@Override
	public Mono<Double> getBookRating(long bookId) {
	
		Mono<Double> rating = reviewSslClient
				.method(HttpMethod.GET)
				.uri(BOOK_RATING + bookId)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve().bodyToMono(Double.class);
		
		return rating;
	}
	
	@Override
	public Mono<Boolean> hasVoted(long bookId, long userId, long voterId) {
			
		Mono<Review> review = reviewSslClient
				.method(HttpMethod.GET)
				.uri(REVIEW_BY_REVIEW_ID + "?bookId=" + bookId + "&userId=" + userId)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve().bodyToMono(Review.class);
		
		return Mono.just(false);
	}
	

	@Override
	public Mono<Boolean> voteHelpful(ReviewVote reviewVote) {
	
		Mono<Boolean> enclume = reviewSslClient
			.method(HttpMethod.POST)
			.uri(ADD_VOTE)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(Mono.just(reviewVote), Boolean.class)
			.retrieve()
			.toEntity(Boolean.class)
			.flatMap(catchErrorsAndTransform3)
			;
		
		
		return enclume;
	}
	
	
	@Override
	public Flux<Review> getReviewsByUserId(long userId) {

		ResponseSpec response = reviewSslClient
		.method(HttpMethod.GET)
		.uri(REVIEWS_BY_USER_ID + "?userId=" + userId)
		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.body(Mono.just(userId), Integer.class).retrieve();
		
		Flux<Review> flux = response.bodyToFlux(Review.class);
			
		return flux;
		
	}
	
	
	Function<ResponseEntity<Boolean>, Mono<Boolean>> catchErrorsAndTransform3 = 
			(ResponseEntity<Boolean> clientResponse) -> {
				if (clientResponse.getStatusCode().is5xxServerError()) {
					throw new UnknownServerException();
				} else if (clientResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					throw new NotFoundException("Review not found");
				} else {
					return Mono.just(clientResponse.getBody());
				}
	};
	
	
	Function<ResponseEntity<Review>, Mono<Review>> catchErrorsAndTransform2 = 
			(ResponseEntity<Review> clientResponse) -> {
				if (clientResponse.getStatusCode().is5xxServerError()) {
					throw new UnknownServerException();
				} else if (clientResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					throw new NotFoundException("Review not found");
				} else {
					return Mono.just(clientResponse.getBody());
				}
	};
	
	
	// helper function returns Mono<String> if OK
	Function<ResponseEntity<ReviewWrap>, Mono<ReviewWrap>> catchErrorsAndTransformCreate = 
						(ResponseEntity<ReviewWrap> clientResponse) -> {
							if (clientResponse.getStatusCode().is5xxServerError()) {
								System.out.println("LAPIN");
								throw new UnknownServerException();
							} else {
								System.out.println("CHEVAL");
								return Mono.just(clientResponse.getBody());
							}
	};


}
