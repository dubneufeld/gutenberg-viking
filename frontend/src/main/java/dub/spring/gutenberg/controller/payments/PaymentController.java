package dub.spring.gutenberg.controller.payments;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.Item;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.Order;
import dub.spring.gutenberg.api.OrderKey;
import dub.spring.gutenberg.api.Payment;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.controller.DisplayItemPrice;
import dub.spring.gutenberg.controller.orders.Checkout;
import dub.spring.gutenberg.service.OrderService;
import dub.spring.gutenberg.service.PaymentService;
import dub.spring.gutenberg.service.UserService;
import dub.spring.gutenberg.utils.DisplayUtils;
import dub.spring.gutenberg.utils.OrderUtils;
import dub.spring.gutenberg.utils.UserUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * I don't use redirection here for a better legibility
 * 
 * */
@Controller
public class PaymentController {
		
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserService userService;
		
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private OrderUtils orderUtils;
		
	@Autowired
	private DisplayUtils displayUtils;
	
	@Autowired 
	private PaymentService paymentService;
	
	
	@RequestMapping(value = "/authorizePayment", method = RequestMethod.POST)
	public Mono<Rendering> authorizePayment(
			@Validated @ModelAttribute("paymentForm") PaymentMethod form,
			Model model,
			final WebSession session) {
		
		int totalSave = (int) session.getAttribute("total");// set in OrderController /payment
			
		Mono<MyUser> user = this.userUtils.getLoggedUser();
			
		Mono<UserDetails> details = user.map(u -> u.getUsername()).flatMap(n -> this.userService.findByUsername(n));
		
		Mono<Collection< ? extends GrantedAuthority>> auths = details.map(u -> u.getAuthorities());
			
		Mono<Order> activeOrder = orderUtils.getActiveOrder().map(ac -> ac.getT1());
		
		// follow
		Mono<OrderKey> orderKey = activeOrder.map(or -> {
			return new OrderKey(or.getUserId(), or.getDate());
		});
		
		// always recalculate
		Mono<Order> order = orderKey.flatMap(key -> orderService.recalculateOrder(key));
		
		Mono<Checkout> checkout = user.map(u -> this.displayUtils.setAdressAndPayMeth(u, model, session));
					
		System.err.println("/payment AD PATRES");
				
		// create a Tuple
		Mono<Tuple2<Order,Checkout>> tuple = Mono.zip(order, checkout);
		
		Mono<Tuple2<Order,Boolean>> result = tuple.flatMap(t -> {
			
			Payment pay = new Payment();
			pay.setAmount(t.getT1().getSubtotal()/100.0);
			pay.setCardNumber(form.getCardNumber());
			pay.setCardName(form.getName());
				
			Mono<Tuple2<Order,Boolean>> allowed = tuple.flatMap(d -> {
				
				if (d.getT2().equals(Checkout.OK) && d.getT1().getSubtotal() == totalSave) {
					// checkout OK, proceed to payment authorization
		
					// follow chain without any discontinuity
					Mono<OrderKey> oKey = order.map(or -> {
								return new OrderKey(or.getUserId(), or.getDate());		
					});		
								
					Mono<Order> chOrder = oKey.flatMap( key -> orderService.checkoutOrder(key));
					
					Mono<Boolean> paymentSuccess = paymentService.authorizePayment(pay)
											.map(p -> {session.getAttributes().put("paymentSuccess", p); return p;});// always return OK
						
					return Mono.zip(chOrder, paymentSuccess);
				} else {
					
					// add more cases
					return Mono.zip(Mono.just(d.getT1()), Mono.just(false));
				}
			
			});// allowed
			
			return Mono.zip(allowed.map(a -> a.getT1()), allowed.map(a -> a.getT2()));
		});
						
		return result.flatMap(ch -> {
			if (ch.getT2()) {// paymentSuccess
				System.err.println("/payment AYATOLLAH " + ch.getT1().getDate());
				
				// finalize order should take place here
				// first retrieve chosen address and payment method
				
				int addressIndex = session.getAttributeOrDefault("addressIndex", 0);		
				Mono<Address> shipAdd = user.map(u -> u.getAddresses().get(addressIndex));
					
				// side effect
				Mono<Order> fOrder = shipAdd.flatMap(add -> orderService.finalizeOrder(ch.getT1(), add, form));
				
				System.err.println("/payment LOREM");
				
				// actual save here
				Mono<Order> sOrder = fOrder.flatMap(or -> orderService.updateOrder(or));// actual persistence	
			
				// here DisplayItemPrice is required
				Flux<DisplayItemPrice> dispItems = sOrder.flatMapMany( or -> displayUtils.getDisplayItemPrices(or));
				
				System.err.println("/payment IPSUM");
				
				// total is required
				Mono<Double> total = sOrder.map(or -> or.getSubtotal() / 100.0);
				
				System.out.println("RETURN SUCCESS");
				
				return Mono.just(Rendering.view("orders/paymentSuccess")						
						.modelAttribute("order", sOrder)
						.modelAttribute("auths", auths)
						.modelAttribute("items", dispItems)
						.modelAttribute("total", total)	
						.build());
			} else {
				return Mono.just(Rendering.view("orders/paymentFailure").build());
			}
		});

	}	
	
}

