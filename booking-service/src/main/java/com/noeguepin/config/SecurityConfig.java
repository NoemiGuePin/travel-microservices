package com.noeguepin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.noeguepin.security.BookingJwtRoleConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	      .csrf(csrf -> csrf.disable())
	      .authorizeHttpRequests(auth -> auth
	        .requestMatchers("/actuator/**").permitAll()
	        .requestMatchers(HttpMethod.GET, "/bookings")
	        	.hasAnyAuthority("booking-user", "booking-admin")
	        .requestMatchers(HttpMethod.POST,   "/bookings/**")
	        	.hasAnyAuthority("booking-user")
            .requestMatchers(HttpMethod.PUT,    "/bookings/**")
                .hasAnyAuthority("booking-user", "booking-admin")	        
            .requestMatchers(HttpMethod.DELETE, "/bookings/**")
                .hasAnyAuthority("booking-user", "booking-admin")	        
	        .anyRequest().permitAll()
	      ).oauth2ResourceServer(oauth -> oauth
	    	.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
	    return http.build();
	  }	
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new BookingJwtRoleConverter());
		return converter;
	}
}
