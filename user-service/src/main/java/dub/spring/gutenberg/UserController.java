package dub.spring.gutenberg;

import static reactor.core.publisher.Mono.error;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dub.spring.gutenberg.api.user.AddressOperations;
import dub.spring.gutenberg.api.user.MyUser;
import dub.spring.gutenberg.api.user.PaymentOperations;
import dub.spring.gutenberg.api.user.Primary;
import dub.spring.gutenberg.exceptions.DuplicateUserException;
import dub.spring.gutenberg.exceptions.InvalidInputException;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.services.UserService;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@DeleteMapping("/deleteUser/{username}")
	public Mono<Void> deleteUser(@PathVariable("username") String username) {
	
		return this.userService.deleteUser(username);
	}
	
	@DeleteMapping("/deleteAllUsers")
	public Mono<Void> deleteAllUsers() {
	
		return this.userService.deleteAllUsers();
	}
	
	@PostMapping(
			value = "/user",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<String> createUser(@RequestBody MyUser body) {
		
		Mono<String> newUser = Mono.just(body).flatMap(transformCreateUser);
		
		return newUser;
	}
	
	
	@GetMapping("/userByName/{username}")
	public Mono<MyUser> getUser(@PathVariable("username") String username) {
		
		return this.userService.findByUsername(username)
				.switchIfEmpty(error(new NotFoundException("No user found " + username )));			
	}
	
	@GetMapping("/userByUserId/{userId}")
	public Mono<MyUser> getUser(@PathVariable("userId") int userId) {
	
		return this.userService.findByUserId(userId)
				.switchIfEmpty(error(new NotFoundException("No user found " + userId )));			
	}
	
	@PutMapping(
			value = "/addAddress",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<MyUser> addAddress(@RequestBody AddressOperations body) {
		
		Mono<AddressOperations> oper = Mono.just(body);
		
		return oper.flatMap(transformAddAddress);
	}
	
	@PutMapping(
			value = "/primaryAddress",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<MyUser> primaryAddress(@RequestBody Primary body) {
			
		Mono<MyUser> user = this.userService.setPrimaryAddress(body.getUserId(), body.getIndex());
		
		return user;
	}
	
	@PutMapping(
			value = "/deleteAddress",
			consumes = "application/json"
			)
	public Mono<MyUser> deleteAddress(@RequestBody AddressOperations body) {
		
		Mono<AddressOperations> oper = Mono.just(body);
		
		return oper.flatMap(transformDeleteAddress);
	}
	
	@PutMapping(
			value = "/addPaymentMethod",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<MyUser> addPayment(@RequestBody PaymentOperations body) {
	
		Mono<PaymentOperations> oper = Mono.just(body);
		
		return oper.flatMap(transformAddPaymentMethod);
	}
	
	@PutMapping(
			value = "/primaryPaymentMethod",
			consumes = "application/json",
			produces = "application/json"
			)
	public Mono<MyUser> primaryPayment(@RequestBody Primary body) {
		
		Mono<MyUser> user = this.userService.setPrimaryPayment(body.getUserId(), body.getIndex());
		
		return user;
		
	}
	
	@PutMapping(
			value = "/deletePaymentMethod",
			consumes = "application/json"
			)
	public Mono<MyUser> deletePaymentMethod(@RequestBody PaymentOperations body) {
	
		Mono<PaymentOperations> oper = Mono.just(body);
		
		return oper.flatMap(transformDeletePaymentMethod);
	}
	
	
	
	Function<MyUser, Mono<String>> transformCreateUser =
			s -> {
					try {
						return userService.createUser(s)
								.flatMap(u -> Mono.just(u.getUsername()));
					} catch (Exception e) {
						System.out.println("COCHON " + e);
						//return Mono.just("SATOR");
						throw new RuntimeException("SATOR");
					}
	};
	
	
	/*
	Function<Throwable, Mono<String>> createUserFallback =
			e -> {
					if (e.getClass().equals(DuplicateUserException.class)) {
						System.err.println("LOREM IPSUUM");
						return Mono.just("CHEVAL");
								
					} else {
						System.err.println("MORBUS GRAVIS");
						return Mono.just("LAPIN");
										
					}
	};*/
	
	// try to clean this stuff, using Mono.error()
	
	Function<AddressOperations, Mono<MyUser>> transformAddAddress = 		
			s -> {
				if (s.getUserId() < 0) {
					throw new InvalidInputException("invalid UserId");
				}
				try {			
					return userService.addAddress(s.getUserId(), s.getAddress());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("SATOR");
				}
	};
	
	
	Function<AddressOperations, Mono<MyUser>> transformDeleteAddress = 
			s -> {
				if (s.getUserId() < 0) {
					throw new InvalidInputException("invalid UserId");
				}
				try {	
					System.out.println("FUTRE " + s.getUserId());
					return userService.deleteAddress(s.getUserId(), s.getAddress());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("SATOR");
				}
	};
	
	Function<PaymentOperations, Mono<MyUser>> transformAddPaymentMethod = 
			s -> {
				if (s.getUserId() < 0) {
					throw new InvalidInputException("invalid UserId");
				}
				try {			
					return userService.addPaymentMethod(s.getUserId(), s.getPaymentMethod());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("SATOR");
				}
	};
	

	Function<PaymentOperations, Mono<MyUser>> transformDeletePaymentMethod = 
			s -> {
				if (s.getUserId() < 0) {
					throw new InvalidInputException("invalid UserId");
				}
				System.err.println("LOREM IPSUM " + s.getUserId());	
				if (s.getUserId() < 0) {
					System.err.println("LOREM IPSUM");	
					throw new InvalidInputException("invalid UserId");
				}
				try {			
					return userService.deletePaymentMethod(s.getUserId(), s.getPaymentMethod());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("SATOR");
				}
	};

}
