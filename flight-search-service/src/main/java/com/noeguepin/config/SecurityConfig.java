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

import com.noeguepin.security.KeycloakRealmRoleConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers(HttpMethod.GET, 
							"/flights/**",
							"/airlines/**",
							"/airports/**"
					).hasRole("flight.read")
			        .requestMatchers(HttpMethod.POST, "/**").hasRole("flight.write")
			        .requestMatchers(HttpMethod.PUT,  "/**").hasRole("flight.write")
			        .requestMatchers(HttpMethod.DELETE,"/**").hasRole("flight.write")
					.anyRequest().authenticated()
					)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
			
		return http.build();

	}
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
		return new JwtAuthenticationConverter();
	}
}
