package dub.spring.gutenberg.utils;

import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.controller.orders.ActiveOrder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface OrderUtils {
	
	public Mono<Tuple2<Order,ActiveOrder>> getActiveOrder();// most tricky part
	
	public Mono<Order> getActiveOrderAlt();// most tricky part

	public Mono<Order> createNewOrder(long userId);// most tricky part
	
	
	//public Mono<OrderKey> getActiveOrderKey(WebSession session);
	public void invalidActiveOrderKey(WebSession session);
}
