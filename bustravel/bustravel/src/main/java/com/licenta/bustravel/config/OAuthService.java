package com.licenta.bustravel.config;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
public class OAuthService {
    public static final String AUTH0_DOMAIN = "https://travel-management-system.eu.auth0.com/";
    public String validateToken(String token){
        Logger logger = LoggerFactory.getLogger(OAuthService.class.getName());
        try {
        JwkProvider provider = new UrlJwkProvider(AUTH0_DOMAIN);

            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(AUTH0_DOMAIN)
                .build();

            jwt = verifier.verify(token);

            return jwt.getSubject();
        } catch (JWTVerificationException e){
            e.printStackTrace();
            return "";
        } catch (JwkException e) {
            e.printStackTrace();
            return "";
        }
    }

}
