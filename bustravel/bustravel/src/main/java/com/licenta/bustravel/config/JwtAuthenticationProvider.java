package com.licenta.bustravel.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final OAuthService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationProvider(OAuthService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
           if (jwtService.isTokenValid(token)) {
                throw new UsernameNotFoundException("Invalid token");
            }
        String id = jwtService.getOAuthId();
        if (id.equals("")) {
            throw new UsernameNotFoundException("Invalid token");
        }

        String username = userDetailsService.loadUserByUsername(id).getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}