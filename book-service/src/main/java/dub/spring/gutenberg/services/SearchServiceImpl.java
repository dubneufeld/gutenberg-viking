package dub.spring.gutenberg.services;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import dub.spring.gutenberg.api.book.Book;
import dub.spring.gutenberg.repository.BookEntity;
import dub.spring.gutenberg.repository.BookRepository;
import reactor.core.publisher.Flux;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private BookMapper mapper;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	ReactiveMongoOperations reactiveMongoOperations;
		
	@Override
	public Flux<Book> searchByDescription(String searchString) {
		// first step: create a Flux of distinct bookIds
		// use a List, not an array
		List<String> tokens = Arrays.asList(searchString.trim().split(" "));
		Flux<Integer> flux = Flux.empty(); 
		for (int i = 0; i < tokens.size(); i++) {
			flux = flux.mergeWith(grungeDescription(tokens.get(i))).distinct();
		}
		
		
		// second step: retrieve all books by bookId
		return flux.flatMap(id -> this.bookRepository.findByBookId(id))
							.map(b-> mapper.entityToApi(b));
	}
	
	
	@Override
	public Flux<Book> searchByTags(String searchString) {
	
		// first step: create a Flux of distinct bookIds
		// use a List, not an array
		List<String> tokens = Arrays.asList(searchString.trim().split(" "));
		Flux<Integer> flux = Flux.empty(); 
		for (int i = 0; i < tokens.size(); i++) {
			flux = flux.mergeWith(grungeTags(tokens.get(i))).distinct();
		}
		
			
		// second step: retrieve all books by bookId
		return flux.flatMap(id -> this.bookRepository.findByBookId(id))
				.map(b-> mapper.entityToApi(b));
	}

	
	@Override
	public Flux<Book> searchByTitle(String searchString) {
	
		// first step: create a Flux of distinct bookIds
		// use a List, not an array
		List<String> tokens = Arrays.asList(searchString.trim().split(" "));
		Flux<Integer> flux = Flux.empty(); 
		for (int i = 0; i < tokens.size(); i++) {
			flux = flux.mergeWith(grungeTitle(tokens.get(i)));
		}
		
			
		// second step: retrieve all books by bookId
		return flux.flatMap(id -> this.bookRepository.findByBookId(id))
				.map(b-> mapper.entityToApi(b));

	}
	
	
	private Flux<Integer> grungeTags(String tok) {
		
		// tags is a List<String>, not a String, so an unwind is needed
		UnwindOperation unwind = unwind("tags");
		
		ProjectionOperation proj1 = project().andExpression("bookId").as("bookId").andExpression("tags").as("tags");
			
		MatchOperation match2 = Aggregation.match(Criteria.where("tags").regex("^.*" + tok + ".*$"));	
					
		ProjectionOperation proj2 = project("bookId");
		
		Aggregation aggregation = Aggregation.newAggregation(proj1, unwind, match2, proj2);
		Flux<BookIdResult> books = reactiveMongoOperations.aggregate(aggregation, "books", BookIdResult.class);						
			
		return books.map(b -> b.getBookId());
	}
	
	private Flux<Integer> grungeTitle(String tok) {
			
		MatchOperation match1 = match(Criteria.where("title").regex("^.*" + tok + ".*$"));				
		ProjectionOperation proj1 = project("bookId");
		
		Aggregation aggregation = Aggregation.newAggregation(match1, proj1);
		Flux<BookIdResult> books = reactiveMongoOperations.aggregate(aggregation, "books", BookIdResult.class);						
		
		return books.map(b -> b.getBookId());
	}
	
	private Flux<Integer> grungeDescription(String tok) {
		
		MatchOperation match1 = match(Criteria.where("description").regex("^.*" + tok + ".*$"));				
		ProjectionOperation proj1 = project("bookId");
		
		Aggregation aggregation = Aggregation.newAggregation(match1, proj1);
		Flux<BookIdResult> books = reactiveMongoOperations.aggregate(aggregation, "books", BookIdResult.class);						
		return books.map(b -> b.getBookId());
	}
}
