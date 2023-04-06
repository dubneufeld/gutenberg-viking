package dub.spring.gutenberg.services;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;

import dub.spring.gutenberg.api.review.Review;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.ReviewNotFoundException;
import dub.spring.gutenberg.repository.ReviewEntity;
import dub.spring.gutenberg.repository.ReviewRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	ReactiveMongoOperations reactiveMongoOperations;
	
	@Autowired
	private ReviewMapper mapper;
	
	
	@Override
	public Mono<Review> createReview(Review body) {
		
		ReviewEntity entity = mapper.apiToEntity(body);
		
		Mono<Review> newEntity = reviewRepository.save(entity)
	              .log()
	              .onErrorMap(
	                  DuplicateKeyException.class,
	                  ex -> new InvalidInputException("Duplicate key, Review Id: " + body.getBookId()))
	              .map(e -> mapper.entityToApi(e));

		return newEntity;
	}

	@Override
	public Mono<Review> getReviewById(int bookId, int userId) {
		Mono<Review> review = this.reviewRepository.findByBookIdAndUserId(bookId, userId)
				.map(r -> mapper.entityToApi(r));
		return review;
	}

	@Override
	public Flux<Review> getReviewsByUserId(long userId) {
		
		Flux<Review> reviews = reviewRepository.findByUserId(userId)
									.map(r -> mapper.entityToApi(r));
		return reviews;
		
	}

	@Override
	public Flux<Review> getReviewByBookId(int bookId, String sortBy) {
				
		Flux<Review> reviews = reviewRepository.findByBookId(
						bookId, 
						Sort.by(Sort.Direction.DESC, sortBy))
									.map(r -> mapper.entityToApi(r));
		return reviews;
		
	}

	@Override
	public Mono<Double> getBookRating(int bookId) {
		GroupOperation group = group("bookId")
				.avg("rating").as("bookRating");

		MatchOperation match = match(new Criteria("bookId").is(bookId));

		// static method, not constructor
		Aggregation aggregation = newAggregation(match, group);
		Flux<BookRating> result = reactiveMongoOperations.aggregate(aggregation, "reviews", BookRating.class);

		Mono<Double> rat = result.next().map(r -> r.getBookRating());
		
		return rat;
	}

	@Override
	public Mono<Boolean> voteHelpful(long userId, int bookId, long voterId, boolean helpful) {
		
		// return true if vote was allowed, false in case of conflict 
		/** Here findAndModify is not correct 
		 * because the modification is allowed only 
		 * if the user has not voted yet
		 */
		
		Mono<ReviewEntity> review = this.reviewRepository.findByBookIdAndUserId(bookId, userId);
		
		Mono<Boolean> hasElement = review.hasElement();
		
		Mono<Boolean> success = hasElement.flatMap(b -> {
			if (!b) {
				return Mono.error(new ReviewNotFoundException());
			} else {
				
				Mono<Boolean> hasVoted = this.hasVoted(review, voterId);
				
				Mono<Boolean> ok = hasVoted.flatMap(hh -> {
					if (hh) {
						// voterId has already voted, do nothing
						
						return Mono.just(false);// not ok
					} else {
						// query preparation
						Query query = new Query();
						Update update = new Update();
						query.addCriteria(Criteria.where("bookId").is(bookId))
							.addCriteria(Criteria.where("userId").is(userId));
						update.inc("helpfulVotes", helpful ? 1 : 0);
						update.addToSet("voterIds", voterId);
						
						// query execution
						Mono<ReviewEntity> entity = reactiveMongoOperations.findAndModify(query, update, 
								new FindAndModifyOptions().returnNew(false), 
								ReviewEntity.class);
						entity.subscribe();// subscribe needed to force query execution
						
						return Mono.just(true);// ok	
					}
				});
				
				return ok;
				
			}});
			
		return success;
	}

	
	private Mono<Boolean>hasVoted(Mono<ReviewEntity> review, final long voterId) {
		
		Mono<Boolean> hasVoted = review.map(rev -> {
			boolean match = false;
			for (int userId : rev.getVoterIds()) {
				if (voterId == userId) {
					match = true;
					break;
				}
			}
			return match;
		});
		
		return hasVoted;
	}

	@Override
	public Mono<Void> deleteReviewById(int bookId, int userId) {
		return this.reviewRepository.deleteAll(reviewRepository.findByBookIdAndUserId(bookId, userId));
		
	}

	@Override
	public Mono<Void> deleteAllReviews() {
		return this.reviewRepository.deleteAll();
	}
	

}
