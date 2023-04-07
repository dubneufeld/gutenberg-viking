package dub.spring.gutenberg;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig {
	
	public ReactiveAuthenticationManagerAdapter anonymousManager() {
	        return new ReactiveAuthenticationManagerAdapter(
	                new ProviderManager(
	                        Arrays.asList(new AnonymousAuthenticationProvider("ANON"))
	                ));  
	}

	    
	@Bean	    
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    
		return http
				//.addFilterBefore(csrfHeaderFilter, SecurityWebFiltersOrder.CSRF)
	            .authorizeExchange()
	            .pathMatchers("/test/**")
	            .permitAll()
	            .pathMatchers("/**")
	            .hasRole("USER")
	            .pathMatchers("/index")
	            .hasRole("USER")
	            .and()
	            .formLogin()
	            .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/home"))
	            //.//.authenticationFailureHandler();
	            .and()
	            .httpBasic()
	            .and()	               
	            .csrf().disable()
	            .build();
	    
	}
	
	
	
}
