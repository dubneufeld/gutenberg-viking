package dub.spring.gutenberg;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class HealthCheckConfiguration {
	
	private final String bookServiceUrl;
	private final String reviewServiceUrl;
	private final String orderServiceUrl;

	private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);
	 
	private final WebClient.Builder webClientBuilder;
    
	private WebClient webClient;
	
	@Autowired
	public HealthCheckConfiguration(
	        WebClient.Builder webClientBuilder,
	        @Value("${app.book-service.host}") String bookServiceHost,
		    @Value("${app.book-service.healthport}") int    bookServicePort,

		    @Value("${app.review-service.host}") String reviewServiceHost,
		    @Value("${app.review-service.healthport}") int    reviewServicePort,

		    @Value("${app.order-service.host}") String orderServiceHost,
		    @Value("${app.order-service.healthport}") int    orderServicePort
	    ) {
	        this.webClientBuilder = webClientBuilder; 
	        bookServiceUrl 		= "http://" + bookServiceHost +   ":" + bookServicePort;
	        reviewServiceUrl 	= "http://" + reviewServiceHost + ":" + reviewServicePort;
	        orderServiceUrl 	= "http://" + orderServiceHost +  ":" + orderServicePort;
		
	}
	
	@Bean 
	ReactiveHealthContributor healthCheckMicroservices() {
		
		final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
		
		registry.put("book", () -> getHealth(bookServiceUrl));
		registry.put("review", () -> getHealth(reviewServiceUrl));
		registry.put("order", () -> getHealth(orderServiceUrl));
		
		return CompositeReactiveHealthContributor.fromMap(registry);
	}
	
	 	
	private Mono<Health> getHealth(String url) {
		        url += "/actuator/health";
		        LOG.debug("Will call the Health API on URL: {}", url);
		        return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
		            .map(s -> new Health.Builder().up().build())
		            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
		            .log();
		    
	}
		
		
	private WebClient getWebClient() {
		if (webClient == null) {
	            webClient = webClientBuilder.build();    
		}
		return webClient;    
	}
		
	
	
}
