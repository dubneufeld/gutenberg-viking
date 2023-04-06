package dub.spring.gutenberg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import dub.spring.gutenberg.api.user.Address;
import dub.spring.gutenberg.api.user.MyUser;
import dub.spring.gutenberg.api.user.PaymentMethod;
import dub.spring.gutenberg.exceptions.DuplicateUserException;
import dub.spring.gutenberg.exceptions.NotFoundException;
import dub.spring.gutenberg.repository.MyUserEntity;
import dub.spring.gutenberg.repository.UserRepository;
import reactor.core.publisher.Mono;

@Component
public class UserServiceImpl implements UserService {
	
	@Autowired 
	private UserRepository userRepository;
	
	@Autowired 
	private MyUserMapper mapper;
	
	@Autowired 
	private ReactiveMongoOperations reactiveMongoOperations;

	@Override
	public Mono<MyUser> findByUsername(String username) {
	
	
		Mono<MyUser> user = userRepository.findByUsername(username)
				.map(u -> mapper.entityToApi(u));
		
		return user;
	}

	
	@Override
	public Mono<MyUser> setPrimaryAddress(long userId, int index) {
		
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);
			
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				 return user.flatMap(u -> {
					u.setMainShippingAddress(index);
					return this.userRepository.save(u)
							.map(uu -> mapper.entityToApi(uu));
				
				 });
			} else {
				
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});
	}

	@Override
	public Mono<MyUser> setPrimaryPayment(long userId, int index) {
		
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);			
		
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				 return user.flatMap(u -> {
					u.setMainPayMeth(index);
					return this.userRepository.save(u)
							.map(uu -> mapper.entityToApi(uu));
				 });
			} else {
				
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});
		
	}
	
	
	@Override
	public Mono<MyUser> addAddress(long userId, Address newAddress) {
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);
	
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				 return user.flatMap(u -> {
					u.getAddresses().add(newAddress);
					//.setMainPayMeth(index);
					return this.userRepository.save(u)
							.map(uu -> mapper.entityToApi(uu));
				 });
			} else {
			
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});
	}

	
	@Override
	public Mono<MyUser> addPaymentMethod(long userId, PaymentMethod newPaymentMethod) {
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);
			
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				 return user.flatMap(u -> {
					u.getPaymentMethods().add(newPaymentMethod);
					return this.userRepository.save(u)
							.map(uu -> mapper.entityToApi(uu));
				 });
			} else {
				
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});
	}
		
	
	@Override
	public Mono<MyUser> deleteAddress(long userId, Address delAddress) {
		
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);
		
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				
				Query query = new Query();
				Update update = new Update();
				
				query.addCriteria(Criteria.where("userId").is(userId));
				update.pull("addresses", delAddress);
				
				Mono<MyUserEntity> newUser = reactiveMongoOperations.findAndModify(
						query, 
						update, 
						new FindAndModifyOptions()
								.returnNew(true),
						MyUserEntity.class);
				return newUser.map(u -> mapper.entityToApi(u));
	
			} else {
				
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});	
	}
	
	
	@Override
	public Mono<MyUser> deletePaymentMethod(long userId, PaymentMethod delPayMeth) {
		
		Mono<MyUserEntity> user = userRepository.findByUserId(userId);
		
		return user.hasElement().flatMap(present -> {
			if (present) {
				System.err.println("BINGO");
				
				Query query = new Query();
				Update update = new Update();
				
				query.addCriteria(Criteria.where("userId").is(userId));
				update.pull("paymentMethods", delPayMeth);
				
				Mono<MyUserEntity> newUser = reactiveMongoOperations.findAndModify(
						query, 
						update, 
						new FindAndModifyOptions()
								.returnNew(true),
						MyUserEntity.class);
				return newUser.map(u -> mapper.entityToApi(u));
	
			} else {
				
				return Mono.error(new NotFoundException("user " + userId + " not found"));
			}
		});	

	}
	
	
	@Override
	public Mono<MyUser> createUser(MyUser user) {
		// check if username already present
	 
		Mono<MyUserEntity> check = userRepository.findByUsername(user.getUsername());
		
		return check.hasElement().flatMap(present -> {
			if (!present) {
				// OK
				
				return this.userRepository.save(this.mapper.apiToEntity(user))
						.map(us -> mapper.entityToApi(us));
			} else {
				// not allowed	
				
				throw new DuplicateUserException(user.getUsername() + " already registered");
				//return Mono.error(new DuplicateUserException());
			}
		});
	}

	@Override
	public Mono<Void> deleteAllUsers() {
		
		return this.userRepository.deleteAll();
	}

	@Override
	public Mono<Void> deleteUser(String username) {
		return this.userRepository.deleteAll(userRepository.findByUsername(username));
	}

	@Override
	public Mono<MyUser> findByUserId(long userId) {
		
		return userRepository.findByUserId(userId)
				.map(u -> mapper.entityToApi(u));
		
	}
	
	
	
}
