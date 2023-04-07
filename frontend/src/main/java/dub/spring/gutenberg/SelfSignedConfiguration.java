package dub.spring.gutenberg;

import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider.SslContextSpec;

@Configuration
public class SelfSignedConfiguration {
	
	@Autowired
	private GutenbergProperties gutenbergProperties;
	
	@Bean 
	SslContext sslContext() throws SSLException {
		
		SslContext context = SslContextBuilder.forClient()
			    .trustManager(InsecureTrustManagerFactory.INSTANCE)
			    .build();
		return context;
	}
	
	@Bean 
	HttpClient httpClient() throws Exception {
		
		HttpClient httpClient = HttpClient.create().secure(grunge);
			
		return httpClient;
	}
	
	
	// WebClient dedicated to book service through edge server
	@Bean 
	WebClient bookSslClient() throws Exception {
			
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient()))
									.baseUrl(gutenbergProperties.getBaseBooksUrl())

									.build();
			
		return client;
	}
	
	// WebClient dedicated to review service through edge server
	@Bean 
	WebClient reviewSslClient() throws Exception {
				
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient()))
										.baseUrl(gutenbergProperties.getBaseReviewsUrl())

										.build();
				
		return client;
	}
	
	// WebClient dedicated to order service through edge server
	@Bean 
	WebClient orderSslClient() throws Exception {
				
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient()))			
										.baseUrl(gutenbergProperties.getBaseOrdersUrl())
										.build();
				
		return client;
	}
	
	// WebClient dedicated to user service through edge server
	@Bean 
	WebClient userSslClient() throws Exception {
					
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient()))
											.baseUrl(gutenbergProperties.getBaseUsersUrl())
											.build();
					
		return client;
	}
	
	// WebClient dedicated to user service through edge server
	@Bean 
	WebClient compositeSslClient() throws Exception {
						
		WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient()))										
											.baseUrl(gutenbergProperties.getCompositeUrl())
											.build();
						
		return client;
	}
	
	// Consumer is a generic interface
	Consumer<SslContextSpec> grunge = new Consumer<SslContextSpec>() {

		@Override
		public void accept(SslContextSpec t) {
			try {
				t.sslContext(sslContext());
			} catch (SSLException e) {
				System.err.println("Exception caught " + e);
			}
		}
	};

}
