package com.noeguepin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OAuth2WebClientConfig {
	
    @Value("${flight-service.base-url}")
    private String flightServiceBaseUrl;
	
    @Bean
    public WebClient flightWebClient(
    		WebClient.Builder builder,
    		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2) {
    	
        return builder
                .baseUrl(flightServiceBaseUrl)
                .filter(oauth2)
                .build();
    }
    
    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository registrations,
            OAuth2AuthorizedClientService authorizedClients) {

        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, authorizedClients);
    }
    
    @Bean
    public ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter(
            AuthorizedClientServiceOAuth2AuthorizedClientManager manager) {

        var oauth = new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
        oauth.setDefaultClientRegistrationId("booking-service");
        return oauth;
    }
    
}
