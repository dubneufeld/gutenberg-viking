package dub.spring.gutenberg.services;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import dub.spring.gutenberg.api.order.Order;
import dub.spring.gutenberg.api.order.OrderKey;
import dub.spring.gutenberg.api.order.OrderState;
import dub.spring.gutenberg.api.order.UserAndReviewedBooks;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.repository.OrderEntity;
import dub.spring.gutenberg.repository.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderMapper mapper;
	
	@Autowired
	private OrderRepository orderRepository;
		
	@Autowired
	private ReactiveMongoOperations reactiveMongoOperations;
	
	//private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Flux<Integer> getBooksNotReviewed(UserAndReviewedBooks userAndReviewedBooks) throws ParseException {
		
		// preparing an aggregation on orders collection
		
		int outLimit = userAndReviewedBooks.getOutLimit();
		
		UnwindOperation unwind = unwind("lineItems");
		
		// set up limit date in application.properties or better add an admin page
		LocalDateTime limitDate = LocalDateTime.of(2010, 
                Month.APRIL, 24, 19, 30, 40);
			
		/** Here I want all orders shipped to the given user */
		MatchOperation match1 = match(Criteria.where("state").is("SHIPPED")
											.and("date").gte(limitDate)
											.and("userId").is(userAndReviewedBooks.getUserId()));
			
		/** Here I want all books not already reviewed by the given user */
		MatchOperation match2 = match(Criteria.where("bookId").nin(userAndReviewedBooks.getReviewedBookIds()));
		
		GroupOperation group = group("bookId").last("userId").as("userId");			
		
		ProjectionOperation proj1 = project("lineItems", "userId");
		
		ProjectionOperation proj2 = project("userId").and("bookId").previousOperation();
		ProjectionOperation projAlias = project("userId")							
					.and("lineItems.bookId").as("bookId");
		
		Aggregation aggregation = null;
		
		if (outLimit > 0) {
			LimitOperation limitOp = limit(userAndReviewedBooks.getOutLimit());		
			aggregation = newAggregation(match1, proj1, unwind, projAlias, group, proj2, match2, limitOp);
		} else {
			aggregation = newAggregation(match1, proj1, unwind, projAlias, group, proj2, match2);
			
		}
		
		Flux<BookUser> bookUsers = reactiveMongoOperations.aggregate(
				aggregation, "orders", BookUser.class);
					
		Flux<Integer> bookIds = bookUsers.map(b -> b.getBookId());
		
		return bookIds;
		
	}

	
	@Override
	public Mono<Order> getOrderByKey(long userId, LocalDateTime date) {
		
		return this.orderFound(userId, date)
				.map(or -> mapper.entityToApi(or));
	}
	
	
	// also here add check user found
	@Override
	public Mono<Order> getActiveOrder(long userId) {
	
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(userId)
											.and("state").nin("PRE_SHIPPING", "SHIPPED"));
		// can be null
		Mono<OrderEntity> doc = reactiveMongoOperations.findOne(query, OrderEntity.class);
		
		return doc.map(or -> mapper.entityToApi(or));
	}
	
	
	@Override
	public Mono<Order> checkoutOrder(long orderId) {
		
		Query query = new Query();	
		query.addCriteria(Criteria.where("id").is(orderId).and("state").is(OrderState.CART));
				
		Update update = new Update();
		update.set("state", OrderState.PRE_AUTHORIZE);
				
		Mono<OrderEntity> doc = reactiveMongoOperations.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), OrderEntity.class);
		
		return doc.map(or -> mapper.entityToApi(or));		
	}

	
	
	
	
	/** Rewrite more compact before next release */
	
	
	

	@Override
	public Mono<Void> deleteAllOrders() {
		return this.orderRepository.deleteAll();
	}
	
	
	
	
	private Mono<OrderEntity> orderFound(long userId, LocalDateTime date) {
		Mono<OrderEntity> order = orderRepository.findByUserIdAndDate(userId, date);	
		Mono<OrderEntity> grunge = order.hasElement().flatMap(found -> {
			if (found) {

				return order;
			} else {
			
				return Mono.error(new NotFoundException("order not found"));
			}
		});//flatMap
	
		return grunge;		
	}

	@Override
	public Flux<OrderKey> getShippedOrdersByUserId(long userId) {
			
		// try to use an Aggregation instead
		
		MatchOperation match1 = match(Criteria.where("userId").is(userId).and("state").is("SHIPPED"));		
		ProjectionOperation proj1 = project("userId", "date");
	
		Aggregation aggregation = newAggregation(match1, proj1);
		
		Flux<OrderKey> keys = reactiveMongoOperations.aggregate(
				aggregation, "orders", OrderKey.class);
	
		return keys;
	}

	@Override
	public Flux<Integer> getBooksBoughtWithBookId(int bookId, int outLimit) {
			
		MatchOperation match1 = match(Criteria.where("state").is("SHIPPED"));		
		ProjectionOperation proj1 = project("lineItems", "userId");
		UnwindOperation unwind = unwind("lineItems");
		MatchOperation match2 = match(Criteria.where("lineItems.bookId").is(bookId));	
		ProjectionOperation proj2 = project("userId");
				
		Aggregation aggregation = Aggregation.newAggregation(match1, proj1, unwind, match2, proj2);
	
		Flux<UserResult> results = reactiveMongoOperations.aggregate(aggregation, "orders", UserResult.class);
		
		Flux<Integer> userIds = results.map(ur -> ur.getUserId());
			
		// second aggregation: find all books SHIPPED to the users returned by the first aggregation
		
		Flux<Flux<BookCount>> bookCounts = forge(userIds, bookId);
				
		Flux<Integer> bookIds = Flux.concat(bookCounts)
				.map(b -> b.getBookId()).distinct().sort();
			
		return bookIds;
	}
	
	
	// utility methods
	Flux<Flux<BookCount>> forge(Flux<Integer> userIds, final int bookId) {
			
			// each Integer is transformed into a Flux<BookCount>
			
			Flux<Flux<BookCount>> temp = userIds.map(
					userId -> {
						
						MatchOperation match1 = match(Criteria.where("state").is("SHIPPED")
								.and("userId").is(userId));	
						GroupOperation group = group("bookId").count().as("count");
						
						ProjectionOperation proj2 = project("count").and("bookId").previousOperation();
						
						ProjectionOperation projAlias = project("userId")							
													.and("lineItems.bookId").as("bookId");
						
						MatchOperation match2 = match(Criteria.where("bookId").ne(bookId));
						UnwindOperation unwind = unwind("lineItems");
						SortOperation sort = sort(Sort.Direction.DESC, "count");
						LimitOperation limitOp = limit(10);
											
						Aggregation aggregation = newAggregation(match1, unwind, projAlias, match2, group, proj2, sort, limitOp);
				
						Flux<BookCount> bookCounts = reactiveMongoOperations.aggregate(
								aggregation, "orders", BookCount.class);

						return bookCounts;
					});
			return temp;
		}

	@Override
	public Mono<Order> updateOrder(Order order) {
			
		//check for presence before update
		Mono<OrderEntity> entity = this.orderFound(order.getUserId(), order.getDate());
		//update all fields of entity
		Mono<OrderEntity> grunge = entity.flatMap(ord -> {
				ord.setLineItems(order.getLineItems());
				ord.setPaymentMethod(order.getPaymentMethod());
				ord.setShippingAddress(order.getShippingAddress());
				ord.setSubtotal(order.getSubtotal());
				ord.setDate(order.getDate());
				ord.setState(order.getState());
				return this.orderRepository.save(ord);
		});
		return grunge.map(or -> mapper.entityToApi(or));
		
	}

	@Override
	public Mono<Order> createOrder(Order order) {
				
		// check that an active order is not already present
		long userId = order.getUserId();
		Mono<Order> activeOrder = getActiveOrder(userId);
		
		Mono<Order> enclume = activeOrder.hasElement().flatMap(p -> {
			
			if (p) {
				return Mono.error(new InvalidInputException("Active order already present"));
			} else {
				Mono<OrderEntity> nOrder = this.orderRepository.save(mapper.apiToEntity(order));
				return nOrder.map(b -> mapper.entityToApi(b));
			}
		});
		
		/**
			if (p) {
				//throw new InvalidInputException("Active order already present");
				return Mono.error(new InvalidInputException("Active order already present"));
			}
		});
		
			Mono<OrderEntity> nOrder = this.orderRepository.save(mapper.apiToEntity(order));
			return nOrder.map(b -> mapper.entityToApi(b));
		
		*/
			return enclume;
	}
	
}
