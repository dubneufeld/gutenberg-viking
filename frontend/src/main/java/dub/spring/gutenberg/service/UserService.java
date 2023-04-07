package dub.spring.gutenberg.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.PaymentMethod;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService {

	// this method belongs to the root interface and should be implemented
	//Mono<UserDetails> findByUsername(String username);
		
	Mono<MyUser> getProfile(String username);
	
	Mono<MyUser> findByUserId(long userId);
	
	Mono<MyUser> deleteAddress(String username, Address address);
	Mono<MyUser> addAddress(String username, Address newAddress);
	Mono<MyUser> makeAddressPrimary(String username, int main);
	
	Mono<MyUser> deletePaymentMethod(String username, PaymentMethod payMeth);
	Mono<MyUser> addPaymentMethod(String username, PaymentMethod newPayMeth);
	Mono<MyUser> makePaymentMethodPrimary(String username, int main);
}
