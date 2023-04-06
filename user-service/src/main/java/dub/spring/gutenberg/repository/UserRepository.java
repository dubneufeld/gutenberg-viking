package dub.spring.gutenberg.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<MyUserEntity, String> {
	
	Mono<MyUserEntity> findByUsername(String username);
	
	Mono<MyUserEntity> findByUserId(long userId);
}
