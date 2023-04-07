package dub.spring.gutenberg;

import java.util.Collections;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

@SpringBootApplication(scanBasePackages = "dub.spring")
public class FrontendApplication {
	
	@Autowired
	private GutenbergProperties gutenbergProperties;
	

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}
	
	
	/*
	@Bean 
	WebClient bookClient() {
		
		// create a dedicated WebClient for each service
		WebClient client = WebClient.builder()
								.baseUrl(gutenbergProperties.getBaseBooksUrl())
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
								.defaultUriVariables(Collections.singletonMap("url", gutenbergProperties.getBaseBooksUrl()))
								.build();
		return client;
	}
	
	
	@Bean 
	WebClient reviewClient() {
		
		// create a dedicated WebClient for each service
		WebClient client = WebClient.builder()
								.baseUrl(gutenbergProperties.getBaseReviewsUrl())
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
								.defaultUriVariables(Collections.singletonMap("url", gutenbergProperties.getBaseReviewsUrl()))
								.build();
		return client;
	}

	
	@Bean 
	WebClient orderClient() {
		
		// create a dedicated WebClient for each service
		WebClient client = WebClient.builder()
								.baseUrl(gutenbergProperties.getBaseOrdersUrl())
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
								.defaultUriVariables(Collections.singletonMap("url", gutenbergProperties.getBaseOrdersUrl()))
								.build();
		return client;
	}

	
	
	@Bean 
	WebClient userClient() {
		
		// create a dedicated WebClient for each service
		WebClient client = WebClient.builder()
								.baseUrl(gutenbergProperties.getBaseUsersUrl())
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
								.defaultUriVariables(Collections.singletonMap("url", gutenbergProperties.getBaseUsersUrl()))
								.build();
		return client;
	}
	
	
	// additional WebClient needed for composite-service
	@Bean 
	WebClient compositeClient() {
			
		// create a dedicated WebClient for each service
		WebClient client = WebClient.builder()
									.baseUrl(gutenbergProperties.getCompositeUrl())
									.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
									.defaultUriVariables(Collections.singletonMap("url", gutenbergProperties.getCompositeUrl()))
									.build();
		return client;
	}
*/
}
