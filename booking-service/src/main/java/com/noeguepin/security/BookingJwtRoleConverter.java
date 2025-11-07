package com.noeguepin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


public class BookingJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt){
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
	
        if (resourceAccess != null) {
            Map<String, Object> bookingClient =
                    (Map<String, Object>) resourceAccess.get("booking-service");

            if (bookingClient != null) {
                Collection<String> roles =
                        (Collection<String>) bookingClient.get("roles");
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                }
            }
        }	

        return authorities;
    }
}
