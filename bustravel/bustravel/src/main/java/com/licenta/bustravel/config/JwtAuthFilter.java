package com.licenta.bustravel.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final OAuthService auth0Service;

    private final UserInfoService userInfoService;
    public JwtAuthFilter(JwtService jwtService, OAuthService auth0Service, UserInfoService userInfoService) {
        this.jwtService = jwtService;
        this.auth0Service = auth0Service;
        this.userInfoService = userInfoService;
    }
    private Logger LOGGER = Logger.getLogger(JwtAuthFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(
            "Bearer ")) {
            String token = authorizationHeader.substring(7);
            String subjectId = auth0Service.validateToken(token);
            LOGGER.log(java.util.logging.Level.INFO, "SubjectId: " + subjectId);
            if (subjectId.equals("")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            username = userInfoService.loadUserByUsername(subjectId).getUsername();
        }
        LOGGER.log(java.util.logging.Level.INFO, "Username: " + username);
        if (username != null && SecurityContextHolder.getContext()
            .getAuthentication() == null) {

            UserDetails userDetails = userInfoService.loadUserByUsername(username);
            LOGGER.info("UserDetails: " + userDetails);
            String token = authorizationHeader.substring(7);
            if (!Objects.equals(auth0Service.validateToken(token), "")) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                LOGGER.info("username: " + authenticationToken.getName());
                SecurityContextHolder.getContext()
                    .setAuthentication(
                        authenticationToken);
            }
        }
        filterChain.doFilter(request,
            response);
    }
}


