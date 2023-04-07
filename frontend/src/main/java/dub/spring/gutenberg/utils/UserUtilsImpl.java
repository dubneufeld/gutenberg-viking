package dub.spring.gutenberg.utils;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.UserAuthority;
import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.controller.orders.AccountCreation;
import dub.spring.gutenberg.service.UserService;
import reactor.core.publisher.Mono;



@Component
public class UserUtilsImpl implements UserUtils {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(UserUtils.class);
		

	@Autowired
	private UserService userService;
	
	@Override
	public Mono<MyUser> getLoggedUser(final WebSession session) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
							
		if (session.getAttribute("loggedUser") != null) {
			
			return Mono.just((MyUser)session.getAttribute("loggedUser"));
		} else {
			
			Mono<MyUser> user = username.flatMap(u -> this.userService.getProfile(u));
			
			return user;
		}
	}
	
	@Override
	public Mono<MyUser> reload(WebSession session) {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();
			
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());						
		Mono<MyUser> user = username.flatMap(u -> this.userService.getProfile(u));
		
		return user;
		
	}
	
	

	
	@Override
	public void setLoggedUser(WebSession session, MyUser user) {
		session.getAttributes().putIfAbsent("loggedUser", user);
		
	}

	@Override
	public Mono<MyUser> createUser(AccountCreation account) {
			
		Address address = new Address(
								account.getStreet(),
								account.getCity(),
								account.getZip(),
								account.getState(),
								account.getCountry());
			
		PaymentMethod payMeth = new PaymentMethod(
								account.getCardNumber(), 
								account.getNameOnCard());
			
			
		UserAuthority authority = new UserAuthority("ROLE_USER");
			
			
		MyUser user = new MyUser();
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setAddresses(Collections.singletonList(address));
		user.setAuthorities(Collections.singleton(authority));
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);
		user.setHashedPassword(account.getHashedPassword());
		user.setMainPayMeth(0);
		user.setMainShippingAddress(0);
		user.setPaymentMethods(Collections.singletonList(payMeth));
		user.setUsername(account.getUsername());
			
		return Mono.just(user);
		 
	}

	@Override
	public Mono<MyUser> getLoggedUser() {
		
		Mono<SecurityContext> context = ReactiveSecurityContextHolder.getContext();		
		Mono<String> username = context.map(c -> c.getAuthentication())
							.map(a -> a.getName());
						
		Mono<MyUser> user = username.flatMap(u -> this.userService.getProfile(u));
		
		Mono<Set<UserAuthority>> auths = user.map(u -> u.getAuthorities());
		
		return user;
		
	}
}
