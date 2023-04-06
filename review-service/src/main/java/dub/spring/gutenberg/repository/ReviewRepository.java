package dub.spring.gutenberg.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveCrudRepository<ReviewEntity, String>{
	
	Mono<ReviewEntity> findByBookIdAndUserId(int bookId, long userId);
	
	Flux<ReviewEntity> findByBookId(int bookId);
	
	Flux<ReviewEntity> findByBookId(int bookId, Sort sort);
	
	Flux<ReviewEntity> findByUserId(long userId);

	
}
