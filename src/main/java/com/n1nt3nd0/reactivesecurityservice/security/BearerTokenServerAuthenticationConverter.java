package com.n1nt3nd0.reactivesecurityservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {
    private final JwtHandler jwtHandler;
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final Function<String, Mono<String>> getTokenValue =
            (authToken) -> Mono.justOrEmpty(authToken.substring(BEARER_TOKEN_PREFIX.length())); // retrieve token after prefix "Bearer "
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return retrieveHeader(exchange) // get Header value "Authorization"
                .flatMap(getTokenValue)  // get token after prefix "Bearer "
                .flatMap(token -> jwtHandler.check(token)) // checking the token for validity
                .flatMap(verificationResult -> UserAuthenticationBearer.createAuthentication(verificationResult)); // return Authentication object
    }
    public Mono<String> retrieveHeader(ServerWebExchange exchange) {
        String headerAuthorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return Mono.justOrEmpty(headerAuthorization);
    }
}
