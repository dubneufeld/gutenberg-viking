package dub.spring.gutenberg.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.api.book.BookUpdate;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.repository.BookEntity;
import dub.spring.gutenberg.repository.BookRepository;
import dub.spring.gutenberg.repository.CategoryEntity;
import dub.spring.gutenberg.repository.CategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookServiceImpl implements BookService {
	
	private final CategoryRepository categoryRepository;
    
	@Autowired
	private BookMapper mapper;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	public BookServiceImpl(
			CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	
	public Mono<Book> createBook(Book body) {
		BookEntity entity = mapper.apiToEntity(body);
		
		Mono<Book> newEntity = bookRepository.save(entity)
	              .log()
	              .onErrorMap(
	                  DuplicateKeyException.class,
	                  ex -> new InvalidInputException("Duplicate key, Book Id: " + body.getBookId()))
	              .map(e -> mapper.entityToApi(e));

		return newEntity;
		
	}
	
	public Mono<Book> getBookBySlug(String slug) {
		
		return bookRepository.findBySlug(slug)
				.map(b -> mapper.entityToApi(b));
	}
	
	public Mono<Book> getBookById(int bookId) {
		return bookRepository.findByBookId(bookId)
				.map(b -> mapper.entityToApi(b));	
	}
	
	public Mono<Void> deleteAllBooks() {
		
		return bookRepository.deleteAll();
	}
	
	public Mono<Void> deleteBook(int bookId) {
		
		bookRepository.delete(bookRepository.findByBookId(bookId).block());
		
		return Mono.empty();
		
	}
	
	@Override
	public Flux<Book> getAllBooksByCategory(String categorySlug, String sortBy) {
		Mono<CategoryEntity> cat = categoryRepository.findBySlug(categorySlug);
		
		Mono<Integer> categoryId = cat.map(c -> c.getCategoryId());
		
		Flux<Book> books = bookRepository.findByCategoryId(
				categoryId, Sort.by(Sort.Direction.ASC, sortBy))
					.map(b -> mapper.entityToApi(b));

		return books;
	}
	
	
	@Override
	public Flux<Book> getAllBooksByBookIdList(List<Integer> bookIds) {
				
		List<Mono<BookEntity>> monoList = new ArrayList<>();
		
		bookIds.forEach(id -> monoList.add(this.bookRepository.findByBookId(id)));
		
		Flux<BookEntity> flux = Flux.concat(monoList);
				
		return flux.map(b-> this.mapper.entityToApi(b));
		
	}


	@Override
	public Mono<Book> updateBookPrice(BookUpdate bookUpdate) {
		int bookId = bookUpdate.getBookId();
		Mono<BookEntity> book = this.bookRepository.findByBookId(bookId);
		return book.flatMap(b -> {
			b.setPrice(bookUpdate.getPrice());
			return this.bookRepository.save(b).map(t -> mapper.entityToApi(t));
		});
	}
	
	
	
}
