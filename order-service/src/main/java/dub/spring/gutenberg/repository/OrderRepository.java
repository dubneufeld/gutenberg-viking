package dub.spring.gutenberg.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveMongoRepository<OrderEntity, String> {
	
	Mono<OrderEntity> findByUserIdAndDate(long userId, LocalDateTime date);
	
	Flux<OrderEntity> findByUserIdAndState(long userId, String state);
}
