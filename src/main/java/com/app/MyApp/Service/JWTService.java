package com.app.MyApp.Service;

import com.app.MyApp.Model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {
    @Value("{jwt.algorithm.key}")
    private String algorithmKey;

    // To verify you were the person who gave the JWT.
    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;
    private static final String USERNAME_KEY="USERNAME";

    private static final String VERIFICATION_EMAIL_KEY="VERIFICATION_EMAIL";

    private static final String RESET_PASSWORD_EMAIL_KEY="RESET_PASSWORD_EMAIL";

    @PostConstruct
    public void postConstruct(){
        algorithm=Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(User user) {
        return JWT.create()
                .withClaim(USERNAME_KEY,user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateJWTForVerification(User user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY,user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateJWTForPasswordReset(User user) {
        return JWT.create()
                .withClaim(RESET_PASSWORD_EMAIL_KEY,user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 30)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token){
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        return jwt.getClaim(USERNAME_KEY)
                .asString();
    }
}
