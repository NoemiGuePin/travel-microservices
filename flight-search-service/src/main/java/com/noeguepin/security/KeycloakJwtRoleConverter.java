package com.noeguepin.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
	
	@Override
	public Collection<GrantedAuthority> convert (Jwt jwt) {
		Set<String> roles = new HashSet<>();
		
		//Realm Roles
		Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> realmRoles) {
            realmRoles.forEach(role -> roles.add(role.toString()));
        }
        
        //Client Roles
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            Map<String, Object> flightService = (Map<String, Object>) resourceAccess.get("flight-service");
            if (flightService != null && flightService.get("roles") instanceof Collection<?> clientRoles) {
                clientRoles.forEach(role -> roles.add(role.toString()));
            }
        }
        
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
		
	}

}
