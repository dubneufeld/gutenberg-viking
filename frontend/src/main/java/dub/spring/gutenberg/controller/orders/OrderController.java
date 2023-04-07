package dub.spring.gutenberg.controller.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.Book;
import dub.spring.gutenberg.api.Item;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.OrderState;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.controller.DisplayItem;
import dub.spring.gutenberg.controller.DisplayItemPrice;
import dub.spring.gutenberg.controller.OrderPriceDisplay;
import dub.spring.gutenberg.controller.payments.PaymentController;
import dub.spring.gutenberg.service.BookService;
import dub.spring.gutenberg.service.OrderAndBook;
import dub.spring.gutenberg.service.OrderService;
import dub.spring.gutenberg.service.UserService;
import dub.spring.gutenberg.utils.DisplayUtils;
import dub.spring.gutenberg.utils.OrderUtils;
import dub.spring.gutenberg.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;


@Controller
public class OrderController {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private OrderUtils orderUtils;
	

	@Autowired
	private DisplayUtils displayUtils;
		
		
	@GetMapping(value ="/getCart")
	public Mono<Rendering> getCart(WebSession session, Model model) {
		
		try {
				
			//Mono<Long> userId = userUtils.getLoggedUser().map(u -> u.getUserId());		
			Mono<Order> activeOrder = orderUtils.getActiveOrderAlt();
			
			// here follow
			Mono<Rendering> grunge = activeOrder.hasElement().flatMap(ac -> {
				
				if (!ac) {			
					// creation, order has no items yet, do nothing
					//Mono<Order> order = userId.flatMap( id -> this.orderUtils.createNewOrder(id));
					
					// from here uses order instead of ac
					//Mono<OrderPriceDisplay> display = order.flatMap(o -> displayUtils.getDisplay(o));
					Mono<OrderPriceDisplay> display = Mono.just(new OrderPriceDisplay());//order.flatMap(o -> displayUtils.getDisplay(o));
					
					
					
					return Mono.just(Rendering.view("orders/cart")
							.modelAttribute("items", display.map(d -> d.getItems()))
							.modelAttribute("subtotal", display.map(d -> d.getSubtotal()))
							.build());					
				} else {
					// existing order, create display 		
					Mono<OrderKey> orderKey = activeOrder.map(or -> new OrderKey(or.getUserId(), or.getDate()));
						
					Mono<String> username = userUtils.getLoggedUser().map(u -> u.getUsername());		
			
					
					// always recalculate
					Mono<Order> order = orderKey.flatMap(key -> orderService.recalculateOrder(key));
				
					// from here uses order instead of ac
					Mono<OrderPriceDisplay> display = order.flatMap(o -> displayUtils.getOrderPriceDisplay(o));
					
					// in addition find all books bought with each book present in cart
					
					// all bookIds in cart
					Mono<List<Long>> allBookIds = order.flatMapMany(or -> Flux.fromIterable(or.getLineItems())).map(it -> it.getBookId())
							.collectList();
						
					// for each bookId present find all books bought with	
					Flux<Book> booksBoughtWith = allBookIds.flatMapMany(transformBoughtWith);
					
							
					return Mono.just(Rendering.view("orders/cart")
							.modelAttribute("username", username)
							.modelAttribute("booksBoughtWith", booksBoughtWith)
							.modelAttribute("items", display.map(d -> d.getItems()))
							.modelAttribute("subtotal", display.map(d -> d.getSubtotal()))
							.build());
			
				}
			});// grunge
			
			return grunge;
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return Mono.just(Rendering.view("accessDenied").build());
		
		} catch (RuntimeException e) {
			logger.warn("Exception caught " + e);
			return Mono.just(Rendering.view("error").build());
		}
	}// /getCart
	
	
	@PostMapping("/addToCart")
	public Mono<Rendering> addToCart(
			@Validated @ModelAttribute("book") Book book) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
				
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());
		Mono<Collection<? extends GrantedAuthority>> auths = context.map(c -> c.getAuthentication().getAuthorities());
		
		Mono<Order> activeOrder = orderUtils.getActiveOrderAlt();
		
		Mono<MyUser> newUser = username.flatMap(u -> this.userService.getProfile(u));
				
		Mono<Long> userId = newUser.map(u -> u.getUserId());
		
		// retrieve book
		Mono<Book> nBook = bookService.getBookBySlug(book.getSlug());
	
		// retrieve existing active order from DB
		Mono<Rendering> grunge = activeOrder.hasElement().flatMap(ac -> {
		
			if (!ac) {	
				
				// create a local Order
				Mono<Order> newOrder = userId.map(id -> {
					Order ord = new Order();
					ord.setDate(LocalDateTime.now()); 
					ord.setUserId(id); 
					ord.setState(OrderState.CART);
					return ord;
				});// newOrder
				
				Mono<Tuple2<Order,Book>> tuple = Mono.zip(newOrder, nBook);
				
				Mono<Order> addOrder = tuple.map(t -> {
					t.getT1().getLineItems().add(new Item(t.getT2().getBookId(),1));
					return t.getT1();
				});
					
				Mono<OrderPriceDisplay> display = addOrder.flatMap(o -> displayUtils.getOrderPriceDisplay(o));
				display = display.map(d -> { d.setSubtotal(d.getItems().get(0).getPrice()); return d;});			
		
				Mono<Order> toto = display.map(d -> d.getOrder()).flatMap(or -> orderService.createOrder(or));
				
				
				Mono<Long> newUserId = toto.map(t -> t.getUserId()); 
						
				Mono<List<Long>> allBookIds = addOrder.flatMapMany(or -> Flux.fromIterable(or.getLineItems())).map(it -> it.getBookId())
						.collectList();
					
				Flux<Book> booksBoughtWith = allBookIds.flatMapMany(transformBoughtWith);
			
				
				// remove unused attributes later
				return Mono.just(Rendering.view("orders/cart")
					
						.modelAttribute("booksBoughtWith", booksBoughtWith)
						.modelAttribute("auths", auths)
						.modelAttribute("items", display.map(d -> d.getItems()))
						.modelAttribute("subtotal", display.map(d -> d.getSubtotal()))
						.modelAttribute("username", username)
						.modelAttribute("user", newUser)
						.modelAttribute("userId", newUserId)
						.modelAttribute("address", new Address())
						.modelAttribute("indexForm", 0)
						.modelAttribute("payMeth", new PaymentMethod())
						.build());
			} else {
				
				// here active order was found in DB, nothing to create
				Mono<Tuple2<Order,Book>> tuple = Mono.zip(activeOrder, nBook);
				
				Mono<OrderAndBook> orderAndBook = tuple.map( t -> {
					OrderAndBook oab = new OrderAndBook();
					oab.setUserId(t.getT1().getUserId());
					oab.setDate(t.getT1().getDate());
					oab.setBookId(t.getT2().getBookId());
					return oab;
				});// orderAndBook
				
				// actual addBookToOrder takes place here
				Mono<Order> orderA = orderAndBook.flatMap(oab -> orderService.addBookToOrder(oab));
				
				// recalculate after adding book is correct
				Mono<OrderKey> orderKey = orderA.flatMap(o -> Mono.just(new OrderKey(o.getUserId(), o.getDate())));	
				Mono<Order> orderR = orderKey.flatMap(key -> orderService.recalculateOrder(key));
		
				Mono<OrderPriceDisplay> display = orderR.flatMap(o -> displayUtils.getOrderPriceDisplay(o));
				display = display.map(d -> { d.setSubtotal(d.getOrder().getSubtotal()/100.0); return d;});			
		
				Mono<Order> toto = display.map(d -> d.getOrder());
					
				Mono<Long> newUserId = toto.map(t -> t.getUserId()); 
			
				Mono<List<Long>> allBookIds = orderR.flatMapMany(or -> Flux.fromIterable(or.getLineItems())).map(it -> it.getBookId())
						.collectList();
					
				Flux<Book> booksBoughtWith = allBookIds.flatMapMany(transformBoughtWith);
					
				return Mono.just(Rendering.view("orders/cart")
						.modelAttribute("items", display.map(d -> d.getItems()))
						.modelAttribute("subtotal", display.map(d -> d.getSubtotal()))
						.modelAttribute("booksBoughtWith", booksBoughtWith)
						.modelAttribute("username", username)
						.modelAttribute("user", newUser)
						.modelAttribute("userId", newUserId)
						.modelAttribute("indexForm", 0)
						.modelAttribute("payMeth", new PaymentMethod())
						.build());
			}
				
		});
		
		return grunge;
				
	}
		
	

	@GetMapping("/payment") 
	public Mono<Rendering> payment(Model model, final WebSession session) {

		// always called with activeOrder already existing, never create a new active order
		System.out.println("allah /payment BEGIN");
		Mono<MyUser> user = userUtils.getLoggedUser();
		
		// get active order here
		Mono<Order> activeOrder = orderUtils.getActiveOrderAlt();
		
		Mono<OrderKey> orderKey = activeOrder.map(or -> {
			return new OrderKey(or.getUserId(), or.getDate());
		});
		
		// always recalculate
		Mono<Order> order = orderKey.flatMap(key -> orderService.recalculateOrder(key));
				
		Flux<DisplayItemPrice> items = orderKey.flatMapMany( key -> this.displayUtils.getDisplayItemPrices(key));
		
		Mono<Double> total = order.map(o -> {
			session.getAttributes().put("total", o.getSubtotal());// side effect
			return o.getSubtotal()/100.0;});
					
		model.addAttribute("total", total);
		model.addAttribute("items", items);
					
		Mono<Checkout> checkout = user.map(u -> this.displayUtils.setAdressAndPayMeth(u, model, session));
		
		return checkout.flatMap(ch -> {
			if (ch.equals(Checkout.OK)) {
				System.err.println("/payment AYATOLLAH " + checkout);
						
				return Mono.just(Rendering.view("orders/payment")
						.modelAttribute("checkout", ch)
						.modelAttribute("addressIndex", session.getAttributeOrDefault("addressIndex", 0))		
						.modelAttribute("payMethIndex", session.getAttributeOrDefault("payMethIndex", 0))
						.build());
			} else if (ch.equals(Checkout.NO_ADDRESS)) {
				
				return Mono.just(Rendering.view("orders/checkoutFailure")
						.modelAttribute("error", "You don't have any address")
						.build());
			} else if (ch.equals(Checkout.NO_PAYMENT_METHOD)) {
				
				return Mono.just(Rendering.view("orders/checkoutFailure")
						.modelAttribute("error", "You don't have any paymentMethod")
						.build());
				
				
			} else {
				return Mono.just(Rendering.view("orders/checkoutFailure").build());
			}
		});
		
	}
	
	
	@GetMapping(value ="/editCart")		
	public Mono<Rendering> getEditCart(
			@RequestParam("redirectUrl") String redirect, WebSession session) {
			
	
		try {
			Mono<Tuple2<Order,ActiveOrder>> activeOrder = orderUtils.getActiveOrder();
			
			// For display only
			// Here both subclasses are required
			Flux<DisplayItem> editItems = activeOrder.flatMapMany(o -> displayUtils.getDisplayItems(o.getT1()));	
			Flux<DisplayItemPrice> items = activeOrder.flatMapMany(o -> displayUtils.getDisplayItemPrices(o.getT1()));
			
			Mono<List<DisplayItem>> lEditItems = editItems.collectList(); 
			
			Mono<OrderEditForm> form = lEditItems.map(l -> {
				OrderEditForm f = new OrderEditForm();
				f.setItems(l);
				return f;
				
			});
			
			session.getAttributes().put("redirectUrl", redirect);
			
			Mono<Double> subtotal = activeOrder.map(o -> o.getT1().getSubtotal()/100.0);
			
			return Mono.just(Rendering.view("orders/editCart")
					.modelAttribute("orderEditForm", form)// already populated form
					.modelAttribute("items", items)
					.modelAttribute("subtotal", subtotal)
					.build());
		} catch (AccessDeniedException e) {
			logger.warn("Exception caught " + e);
			return Mono.just(Rendering.view("accessDenied").build());
		}
	}

	
	@PostMapping(value ="/editCart")
	public Mono<Rendering> editCart(
			@Validated @ModelAttribute("orderEditForm") OrderEditForm orderEditForm, 
			WebSession session, Model model) {		
		try {			
				
				Flux<? extends Item> editItems = Flux.fromIterable(orderEditForm.getItems());
					
				// here use a filter				
				Flux<? extends Item> items = editItems.filter(enclume());
						
				Flux<Item> items2 = items.map(it -> it);
								
				Mono<Tuple2<Order,ActiveOrder>> activeOrder = orderUtils.getActiveOrder();
				
				Mono<OrderKey> orderKey = activeOrder.map(or -> {
					return new OrderKey(or.getT1().getUserId(), or.getT1().getDate());
				});
						
				Mono<Tuple2<OrderKey,List<Item>>> tuple = Mono.zip(orderKey, items2.collectList());
				
				// now update order
				Mono<Order> nOrder = tuple.flatMap(t -> this.orderService.editOrder(t.getT1(), t.getT2()));
						
				Flux<DisplayItemPrice> nItems = nOrder.flatMapMany(or -> this.displayUtils.getDisplayItemPrices(or));		
				Mono<Double> subtotal = nOrder.map(o -> o.getSubtotal()/100.0);
				
				model.addAttribute("items", nItems);
				
				session.getAttributes().put("enclume", "redirect");
				
				return Mono.just(Rendering.view("orders/cart")
						.modelAttribute("items", nItems)
						.modelAttribute("subtotal", subtotal).build());
						
			} catch (AccessDeniedException e) {
				logger.warn("Exception caught " + e);
				return Mono.just(Rendering.view("accessDenied").build());
			}
		
	}
	
	
	// wrapper class
	public static class OrderEditForm {
		
		private List<? extends Item> items;
		
		public OrderEditForm() {
			items = new ArrayList<>();
		}

		public List<? extends Item> getItems() {
			return items;
		}

		public void setItems(List<? extends Item> items) {
			this.items = items;
		}
	
	}
	
	
	public Predicate<Item> enclume() {
  	            
		return t -> t.getQuantity() > 0; 	
	}
	
	public Predicate<Long> distinct() {
  	    
		Map<Long, Boolean> seen = new ConcurrentHashMap<>(); 
		        
		return t -> seen.putIfAbsent(t, Boolean.TRUE) == null; 	
	}
	
	
	Function<List<Long>, Flux<Book>> transformBoughtWith = bookIds -> {
		
		Flux<Book> flux = Flux.empty();
		List<Flux<Book>> list = new ArrayList<>();
		for (long bookId : bookIds) {
			list.add(this.bookService.getBooksBoughtWith(bookId, 5));
			flux = Flux.concat(flux, this.bookService.getBooksBoughtWith(bookId, 5));
		}
		
		return flux;
	};
	
	
}
