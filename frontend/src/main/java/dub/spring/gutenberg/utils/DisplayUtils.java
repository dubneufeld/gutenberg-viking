package dub.spring.gutenberg.utils;

import java.util.List;


import org.springframework.ui.Model;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.controller.OrderDisplay;
import dub.spring.gutenberg.controller.OrderPriceDisplay;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.controller.DisplayItem;
import dub.spring.gutenberg.controller.DisplayItemPrice;
import dub.spring.gutenberg.controller.DisplayOthers;
import dub.spring.gutenberg.controller.orders.Checkout;
import dub.spring.gutenberg.controller.reviews.ReviewWithAuthor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DisplayUtils {

	//Mono<OrderDisplay> getDisplay(Order order);
	
	Mono<OrderPriceDisplay> getOrderPriceDisplay(Order order);
	
	Flux<ReviewWithAuthor> getReviewWithAuthors(Flux<Review> reviews, long userId);
	List<DisplayOthers> getOtherBooksBoughtWith(Order order);

	List<DisplayItem> getDisplayItems(OrderKey orderKey);
	
	Flux<DisplayItemPrice> getDisplayItemPrices(OrderKey orderKey);
	
	Flux<DisplayItem> getDisplayItems(Order order);
	Flux<DisplayItemPrice> getDisplayItemPrices(Order order);
	
	Checkout setAdressAndPayMeth(MyUser user, Model model, WebSession session);
	
}
