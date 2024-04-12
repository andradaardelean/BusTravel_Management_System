package com.licenta.bustravel.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private Set<String> invalidatedTokens = new HashSet<>();

    private Logger LOGGER = LoggerFactory.getLogger(JwtService.class.getName());

    public void invalidateToken(String token){
        invalidatedTokens.add(token);
    }
    public boolean isTokenValid(String token){
        return !invalidatedTokens.contains(token);
    }
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities){
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, username);
    }
    private String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignedKey(){
        byte[] keyBytes = Decoders.BASE64.decode("38792F423F4528482B4B6250655368566D597133743677397A24432646294A40");
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken(String token, UserDetails userDTO){
        final String username = extractUsername(token);
//        final Collection<? extends GrantedAuthority> roles = userDTO.getAuthorities();
//        final List<String> tokenRoles = extractClaim(token, claims -> claims.get("roles", List.class));


        return username.equals(userDTO.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
