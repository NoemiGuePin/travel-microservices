package com.noeguepin.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class AuthUser {
	
    public String sub() {
    	return getToken().getToken().getClaimAsString("sub");
    }
    
    public String email() {
        return getToken().getToken().getClaimAsString("email");
    }

    public String name() {
        var jwt = getToken().getToken();

        String name = jwt.getClaimAsString("name");
        if (name != null) return name;

        name = jwt.getClaimAsString("preferred_username");
        if (name != null) return name;

        return "";
    }
    
    private JwtAuthenticationToken getToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken token) {
            return token;
        }

        throw new IllegalStateException("JWT not present");
    }
}
