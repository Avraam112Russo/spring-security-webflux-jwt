package com.n1nt3nd0.reactivesecurityservice.security;


import com.n1nt3nd0.reactivesecurityservice.exception.authException.AuthException;
import com.n1nt3nd0.reactivesecurityservice.exception.unAuthorizedException.UnAuthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtHandler {
    private final String secret;

    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String token){
        return Mono.just(verify(token))
                .onErrorResume(e -> Mono.error(new UnAuthorizedException(e.getMessage())));
    }
    private VerificationResult verify(String token) {
        Claims claims = getClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();
        if (expirationDate.before(new Date())) {
            throw new RuntimeException("Token expired");
        }
        return new VerificationResult(claims, token);
    }
    public Claims getClaimsFromToken(String token){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getBody();
    }

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }



}
