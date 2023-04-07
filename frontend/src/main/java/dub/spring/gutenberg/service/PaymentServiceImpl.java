package dub.spring.gutenberg.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dub.spring.gutenberg.api.Payment;
import reactor.core.publisher.Mono;


@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger logger = 
			LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Override
	public Mono<Boolean> authorizePayment(Payment payment) {
		logger.warn(Double.toString(payment.getAmount()));
		logger.warn(payment.getCardNumber());
		logger.warn(payment.getCardName());
			
		/** For debugging only */
		if (payment.getCardName().equals("Richard Brunner")) {
			logger.warn("Payment not authorized");
			return Mono.just(false);
		} else {
			System.out.println("TUCKER authorized");
			return Mono.just(true);
		}
	}

}
