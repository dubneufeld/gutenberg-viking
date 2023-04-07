package dub.spring.gutenberg.utils;

import org.springframework.web.server.WebSession;

import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.controller.orders.AccountCreation;
import reactor.core.publisher.Mono;


public interface UserUtils {

	public Mono<MyUser> getLoggedUser(WebSession session);
	
	public Mono<MyUser> getLoggedUser();
	
	public void setLoggedUser(WebSession session, MyUser user);
	
	public Mono<MyUser> reload(WebSession session);
	
	/*
	public MyUser createUser(String username, String password, 
			List<Address> addresses, List<PaymentMethod> paymentMethods,
			UserService userService);
	*/
	public Mono<MyUser> createUser(AccountCreation account);
}