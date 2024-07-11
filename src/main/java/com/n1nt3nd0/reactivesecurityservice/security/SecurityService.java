package com.n1nt3nd0.reactivesecurityservice.security;

import com.n1nt3nd0.reactivesecurityservice.entity.UserEntity;
import com.n1nt3nd0.reactivesecurityservice.exception.authException.AuthException;
import com.n1nt3nd0.reactivesecurityservice.service.UserService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;


    private SecretKey getSignInKey() { // secret key for sign
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private TokenDetails generateToken(UserEntity user){
        Map<String, Object> claims = new HashMap<>(){{
            put("role", user.getRole()); // claims body
            put("username", user.getUsername());
        }};
        return generateToken(claims, String.valueOf(user.getId()));
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject){
        Long expirationDateInMillis = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationDateInMillis);
        return generateToken(expirationDate, claims, subject);
    }
    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdAt = new Date();
        String token = Jwts.builder()
                .claims(claims)
                .issuer(issuer) // avraam112russo
                .subject(subject) // userID
                .issuedAt(createdAt)
                .id(UUID.randomUUID().toString())
                .expiration(expirationDate)
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
        return TokenDetails.builder()
                .token(token)
                .issuedAt(createdAt)
                .expiresAt(expirationDate)
                .build();
    }

    // check username and password from client
    public Mono<TokenDetails>authenticate(String username, String password) {
        return userService.findUserByUsername(username)
                .flatMap(user -> {
                    if ( !user.isEnabled() ){// check user enabled or disabled
                        return Mono.error(new AuthException("Account disabled.", "N!NT3ND0_USER_ACCOUNT_DISABLED"));
                    }
                    if ( !passwordEncoder.matches(password, user.getPassword()) ){ // compare password
                        return Mono.error(new AuthException("Invalid password", "N!NT3ND0_INVALID_PASSWORD"));
                    }
                    return Mono.just(generateToken(user).toBuilder() // generate token
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid username.", "N!NT3ND0_INVALID_USERNAME")));
    }
}
