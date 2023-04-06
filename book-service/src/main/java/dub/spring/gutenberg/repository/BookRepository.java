package dub.spring.gutenberg.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface BookRepository extends ReactiveCrudRepository<BookEntity, String>{
	
	public Mono<BookEntity> findByBookId(int bookId);
	
	public Mono<BookEntity> findBySlug(String slug);
	
	public Flux<BookEntity> findByCategoryId(Mono<Integer> categoryId, Sort sort);
	
	public Flux<BookEntity> findAllByBookId( Flux<Integer> bookIds);
				
		
}
