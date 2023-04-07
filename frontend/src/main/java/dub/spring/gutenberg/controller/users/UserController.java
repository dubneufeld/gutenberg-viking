package dub.spring.gutenberg.controller.users;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * This is the correct way of using ReactiveSecurityContextHolder.
 * Note that username is present only after return.
 * */

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/makeAddressPrimary")
	public Mono<Rendering> makeAddressPrimary(
			@Validated @ModelAttribute("indexForm") IndexForm form) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());
		
		// first create a Tuple2
		Mono<Tuple2<String, Integer>> tuple = Mono.zip(username, Mono.just(form.getIndex()));
		Mono<MyUser> newUser = tuple.flatMap(transformMakeAddressPrimary);
				
		// always return profile page
		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("username", username)
				.modelAttribute("user", newUser)
				.modelAttribute("address", new Address())
				.modelAttribute("indexForm", new IndexForm())
				.modelAttribute("payMeth", new PaymentMethod())
				.build());				
	}
	
	@PostMapping("/makePaymentMethodPrimary")
	public Mono<Rendering> makePaymentMethodPrimary(
			@Validated @ModelAttribute("indexForm") IndexForm form) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
		
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());
		
		// first create a Tuple2
		Mono<Tuple2<String, Integer>> tuple = Mono.zip(username, Mono.just(form.getIndex()));
		Mono<MyUser> newUser = tuple.flatMap(transformMakePaymentMethodPrimary);
				
		// always return profile page
		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("username", username)
				.modelAttribute("user", newUser)
				.modelAttribute("address", new Address())
				.modelAttribute("indexForm", new IndexForm())
				.modelAttribute("payMeth", new PaymentMethod())
				.build());				
	}
	
	
	
	
	@GetMapping(
			value = "/changeShippingAddress")
	public Mono<Rendering> preChangeShippingAddress(
			@RequestParam("redirectUrl") String redirect,
			final WebSession session) {
			
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
		
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());

		Mono<MyUser> user = username.flatMap(transformUser);
		
		// save redirect to session
		session.getAttributes().put("redirect", redirect);
		
		return Mono.just(Rendering.view("users/changeShippingAddress")
				.modelAttribute("indexForm", new IndexForm())
				.modelAttribute("username", username)
				.modelAttribute("user", user)
				.build());					
		
	}
	
	@GetMapping(
			value = "/changePaymentMethod")
	public Mono<Rendering> preChangePaymentMethodShipping(final WebSession webSession) {
			
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());

		Mono<MyUser> user = username.flatMap(transformUser);
		
		return Mono.just(Rendering.view("users/changePaymentMethod")
				.modelAttribute("indexForm", new IndexForm())
				.modelAttribute("username", username)
				.modelAttribute("user", user)
				.build());					
		
	}
	
	
	
	/** 
	 * new version of /selectShippingAddress should return page with redirect
	 * */
	
	@PostMapping(
			value = "/selectShippingAddress")
	public String changeShippingAddress(
			@Validated @ModelAttribute("indexForm") IndexForm form, Model model,
			final WebSession session) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
		
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());

		session.getAttributes().put("addressIndex", form.getIndex());
		
		
		Mono<Address> address = username
									.flatMap(transformUser)
									.map(u -> {
			List<Address> adds = u.getAddresses();
			return adds.get(form.getIndex());
		});
		
		model.addAttribute("address", address);
		
		// only called from payment no ambiguity
		return "redirect:/payment";
		
	}
	
	
	@PostMapping(
			value = "/selectPaymentMethod")
	public String selectPaymentMethod(
			@Validated @ModelAttribute("indexForm") IndexForm form,
			final WebSession session, Model model) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
						.map(a -> a.getName());
		
		session.getAttributes().put("payMethIndex", form.getIndex());
		
		Mono<PaymentMethod> payMeth = username
									.flatMap(transformUser)
									.map(u -> {
			List<PaymentMethod> payMeths = u.getPaymentMethods();
			return payMeths.get(form.getIndex());
		});
		
		model.addAttribute("payMeth", payMeth);
		
		// only called from payment no ambiguity
		return "redirect:/payment";
		/*
		return Mono.just(Rendering.view("users/selectedPaymentMethod")
				.modelAttribute("payMeth", payMeth)
				.build());
				*/
	}
	

	
	@RequestMapping(value = "/getProfile", method = RequestMethod.GET)
	public Mono<Rendering> getProfile(Model model, WebSession session) {
				
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
				
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
							
		Mono<MyUser> user = username.flatMap(transformUser);
	
		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("username", username)
				// here username is present
				.modelAttribute("user", user)
				.modelAttribute("address", new Address())
				.modelAttribute("indexForm", new IndexForm())
			
				.modelAttribute("payMeth", new PaymentMethod())
				.build());			

	}
	
	
	@RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
	public Mono<Rendering> deleteAddress(
			@Validated @ModelAttribute("address") Address form)  {
				
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
	
		// first create a Tuple2
		Mono<Tuple2<String, Address>> tuple = Mono.zip(username, Mono.just(form));
		Mono<MyUser> newUser = tuple.flatMap(transformDeleteAddress);
		
		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("user", newUser).build());
		
	}
	
	
	@GetMapping(value = "/addAddress")
	public String getAddressForm(Model model) {
		model.addAttribute("address", new Address());
		return "users/createAddress";
	}
	
	
	@RequestMapping(value = "/addAddress", method = RequestMethod.POST)
	public Mono<Rendering> postAddress(
			@Validated @ModelAttribute("address") Address form)  {
			
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
		
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
	
		// first create a Tuple2
		Mono<Tuple2<String, Address>> tuple = Mono.zip(username, Mono.just(form));	
		Mono<MyUser> newUser = tuple.flatMap(transformAddAddress);

		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("user", newUser).build());
	}
	
	
	@RequestMapping(value = "/deletePaymentMethod", method = RequestMethod.POST)
	public Mono<Rendering> deletePaymentMethod(
			@Validated @ModelAttribute("payMeth") PaymentMethod form)  {
			
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
					
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
	
		// first create a Tuple2
		Mono<Tuple2<String, PaymentMethod>> tuple = Mono.zip(username, Mono.just(form));
		Mono<MyUser> newUser = tuple.flatMap(transformDeletePayMeth);
		
		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("user", newUser).build());
		
	}
	
	
	@GetMapping(value = "/addPaymentMethod")
	public String getPaymentMethodForm(Model model) {
		model.addAttribute("payMeth", new PaymentMethod());
		return "users/createPaymentMethod";
	}
	
	
	@RequestMapping(value = "/addPaymentMethod", method = RequestMethod.POST)
	public Mono<Rendering> postPaymentMethod(
			@Validated @ModelAttribute("payMeth") PaymentMethod form)  {
				
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
	
		// first create a Tuple2
		Mono<Tuple2<String, PaymentMethod>> tuple = Mono.zip(username, Mono.just(form));	
		Mono<MyUser> newUser = tuple.flatMap(transformAddPayMeth);

		return Mono.just(Rendering.view("users/profile")
				.modelAttribute("user", newUser).build());
	
	}
	
	
	
	Function< String, Mono<List<SelectableAndIndex>> > transformAddIndexes =
			t -> {
		Mono<MyUser> user = this.userService.getProfile(t);
		
		Mono<List<SelectableAndIndex>> toto = user.map(
				u -> {
					List<SelectableAndIndex> list = new ArrayList<>();
					int index = 0;
					for (Address address : u.getAddresses()) {
						list.add(new SelectableAndIndex(address, index++, u.getMainShippingAddress()));
					}
					return list;
				});
		
		return toto;
	};
			
	Function<String, Mono<MyUser>> transformUser = 
			t -> {
				return userService.getProfile(t);
	};
	
			
	Function< String, Mono< Tuple2< List<Address>, List<PaymentMethod>>> > transformUser2 =
			t -> {
				Mono<MyUser> user = this.userService.getProfile(t);
				
				Mono< Tuple2< List<Address>, List<PaymentMethod>>> tuple =
						user.flatMap(u -> {
							List<Address> adds = u.getAddresses();
							List<PaymentMethod> payMeths = u.getPaymentMethods();
							
							return Mono.zip(Mono.just(adds), Mono.just(payMeths));
							
						});
				
				return tuple;
	};
	
			
	Function<Tuple2<String, Address>, Mono<MyUser>> transformDeleteAddress = 
			t -> {
				
				Mono<MyUser> newUser = this.userService.deleteAddress(t.getT1(), t.getT2());
									
				return newUser;
	};
	
	
	Function<Tuple2<String, PaymentMethod>, Mono<MyUser>> transformDeletePayMeth = 
			t -> {
				
				Mono<MyUser> newUser = this.userService.deletePaymentMethod(t.getT1(), t.getT2());
									
				return newUser;
	};
	
	
	Function<Tuple2<String, Address>, Mono<MyUser>> transformAddAddress = 
			t -> {
				
				Mono<MyUser> newUser = this.userService.addAddress(t.getT1(), t.getT2());
							
				return newUser.map(tt -> {System.err.println("MORBUS" + (tt == null)); return tt;});
	};
	
	
	Function<String, Mono<List<PaymentMethod>>> transformPayMeth =
					t -> {			
						Mono<MyUser> user = this.userService.getProfile(t);
						Mono<List<PaymentMethod>> payMeths = user.map(u -> u.getPaymentMethods());
						return payMeths;
	};		
	
	
	Function<String, Mono<List<Address>>> transformAddress =
			t -> {			
				
				return userService.getProfile(t).map(u -> u.getAddresses());
	};
	
	
	Function<Tuple2<String, PaymentMethod>, Mono<MyUser>> transformAddPayMeth = 
			t -> {
				
				Mono<MyUser> newUser = this.userService.addPaymentMethod(t.getT1(), t.getT2());
					
				return newUser.map(tt -> {System.err.println("MORBUS" + (tt == null)); return tt;});
	};
	
	Function<Tuple2<String, Integer>, Mono<MyUser>> transformMakeAddressPrimary = 
			t -> {
				
				Mono<MyUser> newUser = this.userService.makeAddressPrimary(t.getT1(), t.getT2());
					
				return newUser.map(tt -> {System.err.println("MORBUS" + (tt == null)); return tt;});
	};
	
	Function<Tuple2<String, Integer>, Mono<MyUser>> transformMakePaymentMethodPrimary = 
			t -> {
				Mono<MyUser> newUser = this.userService.makePaymentMethodPrimary(t.getT1(), t.getT2());
					
				return newUser.map(tt -> {System.err.println("MORBUS" + (tt == null)); return tt;});
	};
	
	
	
	// wrapper class
	private static class IndexForm {
		
		int index;
		boolean primary = false;

		public int getIndex() {
			return index;
		}

		public boolean isPrimary() {
			return primary;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setPrimary(boolean primary) {
			this.primary = primary;
		}
		
	}
	
}
	
