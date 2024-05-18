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

import static java.util.logging.Level.INFO;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final OAuthService oAuthService;

    private final UserInfoService userInfoService;
    public JwtAuthFilter(OAuthService oAuthService, UserInfoService userInfoService) {
        this.oAuthService = oAuthService;
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
            if(oAuthService.isTokenValid(token)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String subjectId = oAuthService.getOAuthId();
            LOGGER.log(INFO, "SubjectId: " + subjectId);
            if(!subjectId.equals("")){
                username = userInfoService.loadUserByOAuthId(subjectId).getUsername();
            }
        }
        LOGGER.log(INFO, "Username: " + username);
        if (username != null && SecurityContextHolder.getContext()
            .getAuthentication() == null) {

            UserDetails userDetails = userInfoService.loadUserByUsername(username);
            LOGGER.info("UserDetails: " + userDetails);
            String token = authorizationHeader.substring(7);
            if (oAuthService.isTokenValid(token)) {
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


