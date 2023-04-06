package dub.spring.gutenberg.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.mongodb.DuplicateKeyException;

import dub.spring.gutenberg.api.book.Category;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.repository.CategoryEntity;
import dub.spring.gutenberg.repository.CategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private final CategoryRepository categoryRepository;
	private final CategoryMapper mapper;
	
	@Autowired
	public CategoryServiceImpl(
			CategoryRepository categoryRepository,
			CategoryMapper categoryMapper) {
		this.categoryRepository = categoryRepository;
		this.mapper = categoryMapper;
	}
	
	@Override
	public Mono<Category> createCategory(Category body) {
		CategoryEntity entity = mapper.apiToEntity(body);
		
		
		Mono<Category> newEntity = categoryRepository.save(entity)
	              .log()
	              .onErrorMap(
	                  DuplicateKeyException.class,
	                  ex -> new InvalidInputException("Duplicate key, Book Id: " + body.getCategoryId()))
	              .map(e -> mapper.entityToApi(e));

		return newEntity;
	}
	
	@Override
	public Mono<Category> getCategory(int categoryId) {
	
		Mono<Category> cat = categoryRepository.findByCategoryId(categoryId)
				.map(c -> mapper.entityToApi(c));
		
		return cat;
	}
	
	@Override
	public Mono<Category> getCategory(String categorySlug) {
		
		Mono<Category> cat = categoryRepository.findBySlug(categorySlug)
				.map(c -> mapper.entityToApi(c));
		
		return cat;
	}
	
	@Override
	public Mono<Void> deleteCategory(int categoryId) {
		

		
		
		categoryRepository.delete(categoryRepository.findByCategoryId(categoryId).block());
	
		return Mono.empty();
	
		
	
	}
	
	@Override
	public Flux<Category> findAllCategories() {
		return this.categoryRepository.findAll()
				.map(c -> mapper.entityToApi(c));
	}

	@Override
	public Mono<Void> deleteAllCategories() {
		return this.categoryRepository.deleteAll();
	}
	
	

}
