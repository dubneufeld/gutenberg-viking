package dub.spring.gutenberg.services;


import dub.spring.gutenberg.api.user.Address;
import dub.spring.gutenberg.api.user.MyUser;
import dub.spring.gutenberg.api.user.PaymentMethod;
import reactor.core.publisher.Mono;

public interface UserService {

	 Mono<MyUser> findByUserId(long userId);
		
	 Mono<MyUser> findByUsername(String username);
	 
	 Mono<MyUser> setPrimaryAddress(long userId, int index);
		
	 Mono<MyUser> setPrimaryPayment(long userId, int index);
	 
	 Mono<MyUser> addAddress(long userId, Address newAddress);
	 
	 Mono<MyUser> addPaymentMethod(long userId, PaymentMethod newPayment);
		
	 Mono<MyUser> deleteAddress(long userId, Address delAddress);
	 
	 Mono<MyUser> deletePaymentMethod(long userId, PaymentMethod payMeth);
	 
	 Mono<MyUser> createUser(MyUser user);
	 
	 Mono<Void> deleteAllUsers();
	 
	 Mono<Void> deleteUser(String username);
	 
	 
}
