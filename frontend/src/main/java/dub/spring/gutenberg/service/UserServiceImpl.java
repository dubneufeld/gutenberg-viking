package dub.spring.gutenberg.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import dub.spring.gutenberg.UserAuthority;
import dub.spring.gutenberg.api.Address;
import dub.spring.gutenberg.api.AddressOperations;
import dub.spring.gutenberg.api.MyUser;
import dub.spring.gutenberg.api.PaymentMethod;
import dub.spring.gutenberg.api.PaymentOperations;
import dub.spring.gutenberg.api.Primary;
import dub.spring.gutenberg.api.ProfileOperations;
import dub.spring.gutenberg.exceptions.UnknownServerException;
import dub.spring.gutenberg.exceptions.UserNotFoundException;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
	
	private static PasswordEncoder pw = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	private static final String ADD_ADDRESS = "/addAddress"; 
	private static final String DELETE_ADDRESS = "/deleteAddress"; 

	private static final String DELETE_PAYMENT_METHOD = "/deletePaymentMethod"; 
	private static final String ADD_PAYMENT_METHOD = "/addPaymentMethod"; 
	
	private static final String PRIMARY_PAYMENT = "/primaryPaymentMethod";
	private static final String PRIMARY_ADDRESS = "/primaryAddress";
	
	private static final String USER_BY_ID = "/userByUserId/"; 
	private static final String USER_BY_NAME = "/userByName/"; 
	
	
	@Autowired
	WebClient userSslClient;
	
	Map<String, MyUser> users = new HashMap<>();
	
	public UserServiceImpl() {
		
		Set<UserAuthority> auths = new HashSet<>();
		auths.add(new UserAuthority("ROLE_USER"));
		MyUser sator = new MyUser();
		sator.setAccountNonExpired(true);
		sator.setAccountNonLocked(true);
		sator.setEnabled(true);
		sator.setCredentialsNonExpired(true);
		sator.setAuthorities(auths);
		sator.setUsername("sator");
		sator.setHashedPassword(pw.encode("password"));
		users.put("sator", sator);
	}
	
	@Override
	public Mono<UserDetails> findByUsername(String username) {
		
		Mono<MyUser> user = doFindByUsername(username);
			
		Mono<UserDetails> details = user.map(t -> t);
		
		Mono<Collection<? extends GrantedAuthority>> auths = details.map(d -> d.getAuthorities());
	
		return details;
	}
	
	
	@Override
	public Mono<MyUser> getProfile(String username) {
			
		return doFindByUsername(username);
	
	}


	@Override
	public Mono<MyUser> addAddress(String username, Address newAddress) {
		 	
		Mono<MyUser> user = this.getProfile(username);
		
		
		Mono<AddressOperations> enclume = user.map(u -> {
			AddressOperations delOp = new AddressOperations();
			delOp.setAddress(newAddress);
			delOp.setUserId(u.getUserId());
			delOp.setOp(ProfileOperations.ADD);
			return delOp;
		});
			
		Mono<MyUser> toto = userSslClient
			.method(HttpMethod.PUT)
			.uri(ADD_ADDRESS)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(enclume, AddressOperations.class)
			.retrieve()
			.toEntity(MyUser.class)
			.flatMap(catchErrorsAndTransform2)
			;	
		
		return toto;
	
	
	}

	@Override
	public Mono<MyUser> deleteAddress(String username, Address address) {
		 		
		Mono<MyUser> user = this.getProfile(username);
		
		Mono<AddressOperations> enclume = user.map(u -> {
			AddressOperations delOp = new AddressOperations();
			delOp.setAddress(address);
			delOp.setUserId(u.getUserId());
			delOp.setOp(ProfileOperations.DELETE);
			return delOp;
		});
				
		return userSslClient
			.method(HttpMethod.PUT)
			.uri(DELETE_ADDRESS)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(enclume, AddressOperations.class)
			.retrieve()
			.toEntity(MyUser.class)
			.flatMap(catchErrorsAndTransform2);
	
	}

	
	// helper function used as transformer
	Function<ResponseEntity<MyUser>, Mono<MyUser>> catchErrorsAndTransform2 = 
				(ResponseEntity<MyUser> clientResponse) -> {
					if (clientResponse.getStatusCode().is5xxServerError()) {
						throw new UnknownServerException();
					} else if (clientResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
						throw new UserNotFoundException();
					} else {
						return Mono.just(clientResponse.getBody());
					}
		};

	@Override
	public Mono<MyUser> deletePaymentMethod(String username, PaymentMethod payMeth) {
		 			
		Mono<MyUser> user = this.getProfile(username);
			
		Mono<PaymentOperations> enclume = user.map(u -> {
			PaymentOperations delOp = new PaymentOperations();
			delOp.setPaymentMethod(payMeth);
			delOp.setUserId(u.getUserId());
			delOp.setOp(ProfileOperations.DELETE);
			return delOp;
		});
								
		return userSslClient
				.method(HttpMethod.PUT)
				.uri(DELETE_PAYMENT_METHOD)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(enclume, PaymentOperations.class)
				.retrieve()
				.toEntity(MyUser.class)
				.flatMap(catchErrorsAndTransform2);
		
	}


	@Override
	public Mono<MyUser> addPaymentMethod(String username, PaymentMethod newPayMeth) {
			
		Mono<MyUser> user = this.getProfile(username);
		
		Mono<PaymentOperations> enclume = user.map(u -> {
			PaymentOperations addOp = new PaymentOperations();
			addOp.setPaymentMethod(newPayMeth);
			addOp.setUserId(u.getUserId());
			addOp.setOp(ProfileOperations.ADD);
			return addOp;
		});
			
		Mono<MyUser> toto = userSslClient
			.method(HttpMethod.PUT)
			.uri(ADD_PAYMENT_METHOD)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(enclume, AddressOperations.class)
			.retrieve()
			.toEntity(MyUser.class)
			.flatMap(catchErrorsAndTransform2)
			;	
		
		return toto;
		
	}


	@Override
	public Mono<MyUser> makeAddressPrimary(String username, int main) {
		
		Mono<MyUser> user = this.getProfile(username);
		
		Mono<Primary> primary = user.map(u -> {
			
			Primary prim = new Primary();
			prim.setIndex(main);
			prim.setUserId(u.getUserId());
			return prim;
		});
		
		Mono<MyUser> toto = userSslClient
				.method(HttpMethod.PUT)
				.uri(PRIMARY_ADDRESS)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(primary, Primary.class)
				.retrieve()
				.toEntity(MyUser.class)
				.flatMap(catchErrorsAndTransform2)
				;	
		
		return toto;
		
	}


	@Override
	public Mono<MyUser> makePaymentMethodPrimary(String username, int main) {
		
		Mono<MyUser> user = this.getProfile(username);
		
		Mono<Primary> primary = user.map(u -> {
			
			Primary prim = new Primary();
			prim.setIndex(main);
			prim.setUserId(u.getUserId());
			return prim;
		});
		
		Mono<MyUser> toto = userSslClient
				.method(HttpMethod.PUT)
				.uri(PRIMARY_PAYMENT)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(primary, Primary.class)
				.retrieve()
				.toEntity(MyUser.class)
				.flatMap(catchErrorsAndTransform2)
				;	
		
		return toto;
		
	}


	@Override
	public Mono<MyUser> findByUserId(long userId) {

		Mono<MyUser> toto = userSslClient
				.method(HttpMethod.GET)
				.uri(USER_BY_ID + userId)
				.retrieve()
				.toEntity(MyUser.class)
				.flatMap(catchErrorsAndTransform2)
				;	
		
		return toto;
		
	}
	
	

	private Mono<MyUser> doFindByUsername(String username) {
		
		String uri = USER_BY_NAME + username;
		
		WebClient.ResponseSpec enclume = userSslClient
				.method(HttpMethod.GET)
				.uri(USER_BY_NAME + username)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.retrieve();
		
		Mono<MyUser> user = enclume
				.toEntity(MyUser.class) 
				.flatMap(catchErrorsAndTransform2);
				
		Mono<Set<UserAuthority>> auths = user.map(u -> u.getAuthorities());
		
		return user;
	}
	
	
}
