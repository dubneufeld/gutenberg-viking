package dub.spring.gutenberg;

import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.book.Category;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.services.CategoryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CategoryController {
	
	private final CategoryService categoryService;
	
	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("/category/categoryId/{id}")
	public Mono<Category> getCategory(@PathVariable("id") int categoryId) {
		
		Mono<Category> cat = this.categoryService.getCategory(categoryId);
		cat.subscribe(c -> System.out.println(c.getName()));
		
		if (categoryId < 0) {
			throw new InvalidInputException("Invalid bookId: " + categoryId);
		}
		return this.categoryService.getCategory(categoryId)
				.switchIfEmpty(error(new NotFoundException("No category found" + categoryId)));
	}
	
	@GetMapping("/category/slug/{slug}")
	public Mono<Category> getCategoryBySlug(@PathVariable("slug") String slug) {
		
		return this.categoryService.getCategory(slug)
				.switchIfEmpty(error(new NotFoundException("No category found" + slug)));
	}
	
	@GetMapping("/allCategories")
	public Flux<Category> getAllCategories() {
		
		return this.categoryService.findAllCategories();
	
	}
	
	@PostMapping(
			value = "/category",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<Category> createCategory(@RequestBody Category body) {
	
		return this.categoryService.createCategory(body);
	}
	
	@DeleteMapping("/deleteAllCategories")
	public Mono<Void> deleteAllCategory() {
		return this.categoryService.deleteAllCategories();
	}
	
	@DeleteMapping("/category")
	public Mono<Void> deleteCategory(@RequestParam("id") int id) {
		return this.categoryService.deleteCategory(id);
		
	}

}
