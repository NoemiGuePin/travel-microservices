package com.noeguepin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.noeguepin.security.KeycloakJwtRoleConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.out.println("--- EJECUTANDO SECURITY FILTER CHAIN (MVC) ---");
		http
			.csrf(CsrfConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers(HttpMethod.GET, 
							"/flights/**",
							"/airlines/**",
							"/airports/**"
					).permitAll()
					.requestMatchers("/flights/internal/**").hasRole("flight.internal.manage")
			        .requestMatchers(HttpMethod.POST, "/**").hasRole("flight.write")
			        .requestMatchers(HttpMethod.PUT,  "/**").hasRole("flight.write")
			        .requestMatchers(HttpMethod.DELETE,"/**").hasRole("flight.write")
					.anyRequest().authenticated()
					)		
			.oauth2ResourceServer(oauth2 -> 
				oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
			
		return http.build();

	}
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtRoleConverter());
		return converter;
	}
}
