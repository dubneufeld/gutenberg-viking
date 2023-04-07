package dub.spring.gutenberg.utils;

import java.util.ArrayList;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Item;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.api.Review;
import dub.spring.gutenberg.controller.DisplayItem;
import dub.spring.gutenberg.controller.DisplayItemPrice;
import dub.spring.gutenberg.controller.DisplayOthers;
import dub.spring.gutenberg.controller.OrderDisplay;
import dub.spring.gutenberg.controller.OrderPriceDisplay;
import dub.spring.gutenberg.controller.orders.Checkout;
import dub.spring.gutenberg.controller.reviews.ReviewWithAuthor;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.OrderService;
import dub.spring.gutenberg.service.ReviewService;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


@Component
public class DisplayUtilsImpl implements DisplayUtils {

	@Autowired 
	private UserService userService;
	
	@Autowired 
	private OrderService orderService;
	
	@Autowired 
	private BookService bookService;
	
	@Autowired 
	private ReviewService reviewService;

	@Override
	public Flux<ReviewWithAuthor> getReviewWithAuthors(Flux<Review> reviews, long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DisplayOthers> getOtherBooksBoughtWith(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DisplayItem> getDisplayItems(OrderKey orderKey) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public Flux<DisplayItemPrice> getDisplayItemPrices(OrderKey orderKey) {
		Mono<Order> order = orderService.getOrderByOrderKey(orderKey);
	
		Flux<Item> items = order.flatMapMany(or -> Flux.fromIterable(or.getLineItems()));
				
		Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
				-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
		
		Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
			DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
			dip.setBookId(it.getT2().getBookId());
			dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
			return dip;
		});
		
		return itemsWithPrices;
		
	}

	
	// really awkward
	@Override
	public Flux<DisplayItemPrice> getDisplayItemPrices(Order order) {
			
		Flux<Item> items = Mono.just(order).flatMapMany(or -> Flux.fromIterable(or.getLineItems()));
				
		Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
				-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
		
		Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
			DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
			dip.setBookId(it.getT2().getBookId());
			dip.setTitle(it.getT2().getTitle());
			dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
			return dip;
		});
		
		return itemsWithPrices;
		
	}

	@Override
	public Flux<DisplayItem> getDisplayItems(Order order) {
	
		Flux<Item> items = Mono.just(order).flatMapMany(or -> Flux.fromIterable(or.getLineItems()));
		
		Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
				-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
		
		Flux<DisplayItem> displayItems = items2.map(it -> {
			DisplayItem di = new DisplayItem(it.getT1());
			di.setBookId(it.getT2().getBookId());
			di.setTitle(it.getT2().getTitle());
			return di;
		});
		
		return displayItems;
		
	}
		
		
	
	@Override
	public Checkout setAdressAndPayMeth(MyUser user, Model model, WebSession session) {
		
		model.addAttribute("addresses", user.getAddresses());
		model.addAttribute("paymentMethods", user.getPaymentMethods());
		
		model.addAttribute("addressMult", 
				user.getAddresses().size());
			
		model.addAttribute("payMethMult", 
			user.getPaymentMethods().size());
		
		// also store the primary address index
		model.addAttribute("addressIndex", user.getMainShippingAddress());
		model.addAttribute("payMethIndex", user.getMainPayMeth());
			
		// first step: retrieve address details
		Address shipAdd;
		if (session.getAttribute("shipAdd") != null) {// from redirection
			shipAdd = (Address)session.getAttribute("shipAdd");	
		} else if (user.getAddresses() == null || user.getAddresses().size() == 0) {
			model.addAttribute("error", "You don't have any address"); 
			return Checkout.NO_ADDRESS;
		} else {
			shipAdd = user.getAddresses()
							.get(user.getMainShippingAddress());
		}
		model.addAttribute("address", shipAdd);
		
		// second step: retrieve payment method details
		PaymentMethod payMeth;
		if (session.getAttribute("payMeth") != null) {// from redirection
			payMeth = (PaymentMethod)session.getAttribute("payMeth");
		} else if (user.getPaymentMethods() == null || user.getPaymentMethods().size() == 0) {
			model.addAttribute("error", "You don't have any payment method"); 
			return Checkout.NO_PAYMENT_METHOD;
		} else {
			payMeth = user.getPaymentMethods()
							.get(user.getMainPayMeth());
		}
		model.addAttribute("payMeth", payMeth);	
		
		return Checkout.OK;
	}

	@Override
	public Mono<OrderPriceDisplay> getOrderPriceDisplay(Order order) {
			
		List<Item> list = order.getLineItems();
		
		Flux<Item> sourceItems = Flux.fromIterable(list);
			
		Flux<Tuple2<Item, Book>> items2 = sourceItems.flatMap(it 
					-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
			
		Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
				DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
				dip.setBookId(it.getT2().getBookId());
				dip.setTitle(it.getT2().getTitle());
				dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
				return dip;
		});
		
		//orDisp.setSubtotal(order.getSubtotal());
		//orDisp.setItems(i)
			
		Mono<List<DisplayItemPrice>> itemsL = itemsWithPrices.collectList();
			
		Mono<OrderPriceDisplay> disp = itemsL.flatMap(it -> {
				OrderPriceDisplay ds = new OrderPriceDisplay();
				ds.setItems(it);
				ds.setSubtotal(order.getSubtotal()/100.0);
				ds.setUserId(order.getUserId());
				ds.setOrder(order);
				return Mono.just(ds);
			});// disp
			
			//return disp;
			//return Mono.empty();
		//});// orDisp
		
		
		//return Mono.empty();
		return disp;
	}
		
		/*
		Mono<Order> orderM = Mono.just(order);
		
		Mono<OrderDisplay> orDisp = orderM.flatMap(or -> {
			
			List<Item> items = or.getLineItems();
	
			Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
					-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
			
			Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
				DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
				dip.setBookId(it.getT2().getBookId());
				dip.setTitle(it.getT2().getTitle());
				dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
				return dip;
			});
			
			Mono<List<DisplayItemPrice>> itemsL = itemsWithPrices.collectList();
		
			Mono<OrderDisplay> disp = items.map(it -> {
				OrderDisplay ds = new OrderDisplay();
				ds.setItems(items);
				ds.setSubtotal(or.getSubtotal());
				
			
			
			return Mono.just(ds);
		});
		
		return orDisp;
		
		/*
		Flux<Item> items = orderM.flatMapMany(or -> Flux.fromIterable(or.getLineItems()));
		
		Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
				-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
		
		Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
			DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
			dip.setBookId(it.getT2().getBookId());
			dip.setTitle(it.getT2().getTitle());
			dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
			return dip;
		});
		
		Mono<List<DisplayItemPrice>> itemsL = itemsWithPrices.collectList();
		
		Mono<OrderDisplay> disp = itemsL.map(it -> {
			OrderDisplay ds = new OrderDisplay();
			ds.setItems(it);
			ds.setSubtotal(order.getSubtotal());
			return ds;
		});
		
		
		
		return disp;
		*/
		//return Mono.empty();
	//}

	/*
	@Override
	public Mono<OrderPriceDisplay> getDisplay(OrderDisplay disp) {
		
		Mono<OrderDisplay> dispM = Mono.just(disp);
		
		Mono<OrderPriceDisplay> orDisp = dispM.flatMap(ds -> {
			
			List<Item> items = ds.getItems();
			
			Flux<Item> itemsF = Flux.fromIterable(items);
			
			Flux<Tuple2<Item, Book>> items2 = itemsF.flatMap(it 
					-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
			
			Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
				DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
				dip.setBookId(it.getT2().getBookId());
				dip.setTitle(it.getT2().getTitle());
				dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
				return dip;
			});	
			
			Mono<OrderPriceDisplay> enclume = itemsWithPrices.collectList().map(it -> {
				OrderPriceDisplay grunge = new OrderPriceDisplay();
					grunge.setItems(it);
					grunge.setSubtotal(ds.getSubtotal());
				return grunge;	
			});
				
			
			
			
			return enclume;
		});//orDisp
			
			/*
			OrderPriceDisplay opd = new OrderPriceDisplay();
			
			List<Item> items = ds.getItems();
			
			Flux<Item> itemsF = Flux.fromIterable(items);
			
			Flux<Tuple2<Item, Book>> items2 = itemsF.flatMap(it 
					-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
			
			Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
				DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
				dip.setBookId(it.getT2().getBookId());
				dip.setTitle(it.getT2().getTitle());
				dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
				return dip;
			});	
			
			Mono<OrderPriceDisplay> disp = items.map(it -> {
			OrderPriceDisplay ds = new OrderDisplay();
			//	ds.setItems(items);
			//	ds.setSubtotal(or.getSubtotal());
			
			
			return Mono.empty();	
		});
		
	
			/*
			Flux<Tuple2<Item, Book>> items2 = items.flatMap(it 
					-> Mono.zip(Mono.just(it), bookService.getBookByBookId(it.getBookId())));
			
			Flux<DisplayItemPrice> itemsWithPrices = items2.map(it -> {
				DisplayItemPrice dip = new DisplayItemPrice(it.getT1());
				dip.setBookId(it.getT2().getBookId());
				dip.setTitle(it.getT2().getTitle());
				dip.setPrice(it.getT1().getQuantity() * it.getT2().getPrice() / 100.0);
				return dip;
			});
			
			Mono<List<DisplayItemPrice>> itemsL = itemsWithPrices.collectList();
			*/
			//Mono<OrderDisplay> disp = items.map(it -> {
			//	OrderDisplay ds = new OrderDisplay();
			//	ds.setItems(items);
			//	ds.setSubtotal(or.getSubtotal());
				
		
		//return Mono.empty();
		
	//}
	
}
