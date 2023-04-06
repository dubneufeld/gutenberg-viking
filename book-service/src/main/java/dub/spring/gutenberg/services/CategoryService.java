package dub.spring.gutenberg.services;

import dub.spring.gutenberg.api.book.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CategoryService {
	
	public Flux<Category> findAllCategories();
	
	public Mono<Category> getCategory(String categorySlug);
	
	public Mono<Category> getCategory(int categoryId);
	
	public Mono<Category> createCategory(Category body);
		
	public Mono<Void> deleteCategory(int id);
	
	public Mono<Void> deleteAllCategories();
}
