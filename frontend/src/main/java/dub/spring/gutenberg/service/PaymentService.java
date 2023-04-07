package dub.spring.gutenberg.service;

import org.springframework.security.access.prepost.PreAuthorize;

import dub.spring.gutenberg.api.Payment;
import reactor.core.publisher.Mono;


@PreAuthorize("isFullyAuthenticated()")
public interface PaymentService {

	public Mono<Boolean> authorizePayment(Payment payment); 
}
