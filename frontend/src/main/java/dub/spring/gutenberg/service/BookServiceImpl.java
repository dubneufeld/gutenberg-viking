package dub.spring.gutenberg.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Category;
import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.exceptions.BookNotFoundException;
import dub.spring.gutenberg.exceptions.UnknownServerException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class BookServiceImpl implements BookService {
	
	private static final String BOOK_BY_SLUG = "/book/slug/";
	private static final String BOOKS_BY_CATEGORY = "/allBooksByCategory/";
	private static final String BOOKS = "/books/";
	private static final String BOOK_BY_ID = "/book/";// correct laater	
	private static final String BOOKS_BOUGHT_WITH = "/getBooksBoughtWithBookId";//remove RequestParameter later
	private static final String SORT_BY = "/sortBy/";
	private static final String OUT_LIMIT = "/outLimit/";
	private static final String BOOKS_NOT_REVIEWED = "/getBooksNotReviewed";
	private static final String ALL_CATEGORIES = "/allCategories";
	private static final String CATEGORY = "/category/slug/";

	
	@Autowired
	private WebClient bookSslClient;
	
	@Autowired
	private WebClient compositeSslClient;
			
	
	
	@Override
	public Mono<Category> getCategory(String categorySlug) {
		System.err.println("BINGO");
		Mono<Category> cat = bookSslClient
				.method(HttpMethod.GET)
				.uri(CATEGORY + categorySlug)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve().bodyToMono(Category.class);
	
		return cat;
	}
	

	@Override
	public Mono<Book> getBookBySlug(String slug) {
		
		WebClient.ResponseSpec enclume = bookSslClient
				.method(HttpMethod.GET)
				.uri(BOOK_BY_SLUG + slug)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve();
		
		Mono<Book> book = enclume.bodyToMono(Book.class);

		return book;
	}
	
	
	@Override
	public Mono<Book> getBookByBookId(long bookId) {
		
		System.out.println("getBookByBookId " + bookId);
		
		WebClient.ResponseSpec enclume = bookSslClient
				.method(HttpMethod.GET)
				.uri(BOOK_BY_ID + bookId)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve();
		
		Mono<Book> book = enclume.bodyToMono(Book.class);
		
		return book;

	}
	
		
	public Flux<Book> allBooksByCategory(String categorySlug, String sortBy) {
			
		String allBooksUri = BOOKS_BY_CATEGORY + categorySlug + SORT_BY + sortBy;
		
		WebClient.RequestBodySpec requestSpec = bookSslClient
				.method(HttpMethod.GET)		
				.uri(allBooksUri);
		
				WebClient.ResponseSpec response = requestSpec.retrieve();
	
				Flux<Book> flux = response.bodyToFlux(Book.class);	
				
				return flux;
	}
	
	
	@Override
	public Flux<Book> getBooksBoughtWith(long bookId, int outLimit) {
		
		String booksBoughtWithUri = BOOKS_BOUGHT_WITH + "?bookId=" + bookId;
		WebClient.RequestBodySpec requestSpec = compositeSslClient
				.method(HttpMethod.GET)		
				.uri(booksBoughtWithUri);//BOOKS_BOUGHT_WITH + bookId + OUT_LIMIT + outLimit);

				WebClient.ResponseSpec response = requestSpec.retrieve();
				
				Flux<Book> flux = response.bodyToFlux(Book.class);	
			
				return flux;
	
	}

			
	@Override
	public Flux<Book> getBooksNotReviewed(long userId, int outLimit) throws ParseException {
			
		/**
		 * Only call composite-service that does the job
		 * */
	
		String booksNotReviewedUrl = BOOKS_NOT_REVIEWED + "?userId=" + userId + "&outLimit=5";
		
		WebClient.ResponseSpec enclume = compositeSslClient
				.method(HttpMethod.GET)
				.uri(booksNotReviewedUrl)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)		
				.retrieve();
		
		Flux<Long> bookIds = enclume.bodyToFlux(Long.class);
			
		Flux<Book> booksToReview = bookIds.flatMap(b -> getBookByBookId(b));
		
		return booksToReview;
			
	}
	
	
	@Override
	public Flux<Book> searchBookByTitle(String searchString) {
					
		return this.searchBook(searchString, "/searchByTitle");
	}
	
	
	@Override
	public Flux<Book> searchBookByDescription(String searchString) {
						
		return this.searchBook(searchString, "/searchByDescription");
	}
	
	@Override
	public Flux<Book> searchBookByTags(String searchString) {
					
		return this.searchBook(searchString, "/searchByTags");
	}
	
	
	private Flux<Book> searchBook(String searchString, String booksURI) {
				
	
		BookSearch bookSearch = new BookSearch();
		bookSearch.setSearchString(searchString);
				
		WebClient.ResponseSpec responseSpec = bookSslClient
				.method(HttpMethod.POST)		
				.uri(booksURI)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		.body(Mono.just(bookSearch), BookSearch.class)		
		.retrieve();
			
		Flux<Book> flux = responseSpec.bodyToFlux(Book.class);	
		Flux<Book> blist = flux.sort(new Comparator<Book>() {

			@Override
			public int compare(Book o1, Book o2) {
				return (o1.getBookId() < o2.getBookId()) ? -1 : ((o1.getBookId() > o2.getBookId()) ? 1 : 0);				
			}});

		return blist;	
	}
	
	
	/** helper functions used as transformers */
	
	
	// helper function returns Mono<Book> if OK
	Function<ClientResponse, Mono<Book>> catchErrorsAndTransform = 
					(ClientResponse clientResponse) -> {
						if (clientResponse.statusCode().is5xxServerError()) {
							throw new UnknownServerException();
						} else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
							throw new BookNotFoundException();
						} else {
							return clientResponse.bodyToMono(Book.class);
						}
	};
					
	// helper function returns Mono<BookWebList> if OK
	Function<ClientResponse, Mono<BookWebList>> catchErrorsAndTransformList = 
					(ClientResponse clientResponse) -> {
						if (clientResponse.statusCode().is5xxServerError()) {
							throw new UnknownServerException();				
						} else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
							throw new BookNotFoundException();
						} else {
							return clientResponse.bodyToMono(BookWebList.class);
						}
					};

	// helper function returns Flux<String> if OK
	Function<ClientResponse, Flux<String>> catchErrorsAndTransformFlux = 
					(ClientResponse clientResponse) -> {
						if (clientResponse.statusCode().is5xxServerError()) {
							throw new UnknownServerException();				
						} else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
							throw new BookNotFoundException();
						} else {
							return clientResponse.bodyToFlux(String.class);
						}				
	};

	
	
	
	@Override
	public Flux<Category> getLeaveCategories() {
			
		WebClient.RequestBodySpec requestSpec = bookSslClient
					.method(HttpMethod.GET)
					.uri(ALL_CATEGORIES);
			
		WebClient.ResponseSpec response = requestSpec.retrieve();
		
		// all categories
		Flux<Category> flux = response.bodyToFlux(Category.class);	
		
		Flux<Category> leaves = flux.filterWhen(cat -> Mono.just(cat.getChildren().size() == 0));
			
		return leaves;					
	}

	
		
}
