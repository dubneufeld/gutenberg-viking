package dub.spring.gutenberg.utils;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderState;
import dub.spring.gutenberg.controller.orders.ActiveOrder;
import dub.spring.gutenberg.service.OrderService;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


@Component
public class OrderUtilsImpl implements OrderUtils {

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserUtils userUtils;
	

	@Override
	public void invalidActiveOrderKey(WebSession session) {
		if (session.getAttribute("activeOrderKey") != null) {
			session.getAttributes().put("activeOrderKey", null);
		}
	}


	@Override
	public Mono<Tuple2<Order,ActiveOrder>> getActiveOrder() {
		
		System.out.println("muhammad getActiveOrder BEGIN");
		
		Mono<MyUser> user = this.userUtils.getLoggedUser();
		
		Mono<Long> userId = user.map(u -> u.getUserId());
		
		Mono<Order> activeOrderTry = userId.flatMap(id -> this.orderService.getActiveOrder(id));
	
		// here activeOrder may be null
		Mono<Tuple2<Order,ActiveOrder>> activeOrder = activeOrderTry.hasElement().flatMap(p -> {
						
						if (p) {
							// active order found in DB
							System.out.println("OrderUtils getActiveOrder active order found in DB");						
							return Mono.zip(activeOrderTry, Mono.just(ActiveOrder.EXISTING));
						} else {
							System.out.println("OrderUtils getActiveOrder create new active order");	
							Mono<Order> or = userId.map(id -> {
								Order ord = new Order();
								ord.setDate(LocalDateTime.now());
								ord.setUserId(id);
								ord.setState(OrderState.CART);
								return ord;
							});
								
							return Mono.zip(or.flatMap(o -> this.orderService.createOrder(o)), Mono.just(ActiveOrder.CREATED));
						}
						
					});
						
		return activeOrder;
	}


	@Override
	public Mono<Order> getActiveOrderAlt() {
				
		System.out.println("muhammad getActiveOrder BEGIN");
			
		Mono<MyUser> user = this.userUtils.getLoggedUser();
			
		Mono<Long> userId = user.map(u -> u.getUserId());
			
		Mono<Order> activeOrder = userId.flatMap(id -> this.orderService.getActiveOrder(id));
											
		return activeOrder;

	}


	@Override
	public Mono<Order> createNewOrder(long userId) {
		
		System.out.println("OrderUtils createNewOrder");
		Order ord = new Order();
		ord.setDate(LocalDateTime.now());
		ord.setUserId(userId);
		ord.setState(OrderState.CART);
		return this.orderService.createOrder(ord);
	}
	
	
	/*
	Function<Tuple3<Order,Long,Boolean>,Mono<Order>> transformCreationAlt =
			t -> {
				
				//Mono<Order> ord = t.flatMap(u -> this.orderService.getActiveOrder(u));
				
				//Mono<Boolean> pres = t.getT1().h
				
				Mono<Order> order = t.getT1().m.flatMap(p -> {if (p == true) 
								{return t;} else {
									Order ord = new Order();
									ord.setState(OrderState.CART);
									ord.setUserId(user.getUserId());
									//order.setDate(LocalDateTime.now());
									
									return Mono.empty();
								} });
				
				return order;
			};
	
	Function<Mono<Order>,Mono<Order>> transformCreation =
			t -> {
				
				Mono<Boolean> pres = t.hasElement();
				
				Mono<Order> order = pres.flatMap(p -> {if (p == true) 
								{return t;} else {
									Order ord = new Order();
									ord.setState(OrderState.CART);
									//ord.setUserId(user.getUserId());
									//order.setDate(LocalDateTime.now());
									
									return Mono.empty();
								} });
				
				return order;
			};
	
	*/
}