package dub.spring.gutenberg.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<CategoryEntity, String>{
	
	public Mono<CategoryEntity> findByCategoryId(int categoryId);
	
	public Mono<CategoryEntity> findBySlug(String slug);
	
}
